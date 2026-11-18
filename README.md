# wrench — Java 业务工具组件库

一套面向 Spring Boot 业务的通用组件库，groupId 统一为 `cn.zachqie.wrench`，版本 `1.0.0`。

**环境要求**：JDK 8+、Maven 3.6+。

## 模块总览

| 模块 | artifactId | 作用 | 依赖 |
|------|-----------|------|------|
| 基础层 | `base` | 统一断言、响应体、分页/排序模型 | 无 |
| 工具集 | `util` | 日期、ID、HTTP、反射、RequestId 等工具 | `base` |
| 认证 | `jwt` | JWT token 生成/解析、用户上下文 | `base` |
| 规则树 | `ruletree` | 策略路由（责任链 + 多线程模板） | 无 |
| 决策树 | `decisiontree` | 可编排决策树引擎 | 无 |
| 状态机 | `statemachine` | 状态机抽象框架 | 无 |
| 测试 | `test-case` | 各模块的单元测试示例 | 全部模块 |

模块依赖关系：只有 `util` 和 `jwt` 依赖 `base`；`ruletree`、`decisiontree`、`statemachine` 零依赖，可以单独引入。

## 快速接入

在业务工程的 `pom.xml` 中按需引入（以 `util` + `jwt` 为例）：

```xml
<dependency>
    <groupId>cn.zachqie.wrench</groupId>
    <artifactId>util</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>cn.zachqie.wrench</groupId>
    <artifactId>jwt</artifactId>
    <version>1.0.0</version>
</dependency>
```

本地安装：在 `wrench/` 目录执行 `mvn clean install`，全部模块会装入本地仓库。

---

## base — 基础层

### Assert 断言

全部静态方法，条件不满足时抛 `InvalidRuntimeException`，可附带 data：

```java
import cn.zachqie.wrench.base.Assert;
import cn.zachqie.wrench.base.InvalidRuntimeException;

try {
    Assert.notNull(param, "param is null", relatedData);  // data 随异常抛出
    Assert.notZero(count, "count is zero");               // null 或 0 都不通过
    Assert.notEmpty(list, "list is empty");               // null 或空集合都不通过
    Assert.notBlank(name, "name is blank");               // null、空串、空白都不通过
    Assert.isTrue(flag, "must be true");                   // null 视为不通过
    Assert.isFalse(flag, "must be false");                 // null 视为不通过
    Assert.checkGeTargetNum(new BigDecimal("5"), new BigDecimal("3"), "must >= 3"); // 任一为 null 时通过
} catch (InvalidRuntimeException e) {
    Object data = e.getData();   // 拿回附带的 data
    String msg = e.getMessage();
}
```

### ResponseResult 统一响应体

```java
import cn.zachqie.wrench.base.ResponseResult;

ResponseResult<User> ok = ResponseResult.ok("success", user);      // code=0
ResponseResult<User> fail = ResponseResult.fail("some error");      // code=-1
ResponseResult<User> custom = ResponseResult.build(1001, "msg", user, requestId);

if (result.successful()) { ... }   // code == 0 即 successful
Long code = result.getCode();      // Integer
String msg = result.getMsg();
User data = result.getData();
```

### PageProperty / PageResult / OrderProperty 分页与排序

```java
import cn.zachqie.wrench.base.PageProperty;
import cn.zachqie.wrench.base.PageResult;
import cn.zachqie.wrench.base.OrderProperty;

// 分页入参：pageNum 默认 1，pageSize 默认 100、上限 1000（非法值自动回退默认）
PageProperty pageProperty = new PageProperty(2, 20);

// 分页结果
PageResult<User> page = PageResult.buildPageResult(pageProperty, totalRows, dataList);
page.getTotalPages();      // 总页数（自动向上取整）
page.isFirstPage();        // 第 1 页
page.isLastPage();        // 最后一页
page.isHasNextPage();      // pageNum < totalPages
page.isHasPreviousPage();  // pageNum > 1
page.getList();

// 空页
PageResult<User> empty = PageResult.emptyPageResult(1, 20);

// 排序入参（链式）
OrderProperty order = new OrderProperty();
order.orderDesc("id").orderAsc("name");
boolean has = order.hasOrders();           // 是否有有效排序
List<OrderProperty.Order> orders = order.getOrders();
```

---

## util — 工具集

### DateUtils 日期

```java
import cn.zachqie.wrench.util.DateUtils;
import java.util.Date;

Date date = DateUtils.parseDate(1794931200000L);        // 时间戳 -> Date，null 返回 null
String ymd = DateUtils.formatDate(date, DateUtils.DateFormat.yyyy_MM_dd);
String full = DateUtils.formatDate(date, DateUtils.DateFormat.yyyy_MM_dd_HH_mm_ss);
String empty = DateUtils.formatDate(null);               // ""，不抛异常

Date begin = DateUtils.getDateBegin(date);               // 当天 00:00:00
Date tomorrow = DateUtils.plusDays(date, 1);
Date yesterday = DateUtils.minusDays(date, 1);
Date plus2h = DateUtils.plusHours(date, 2);

boolean same = DateUtils.isSameDay(date, other);         // 任一为 null 返回 false
boolean today = DateUtils.isSameDay4Now(date);           // 是否与今天同天
```

### IdGeneratorUtils ID 生成

```java
import cn.zachqie.wrench.util.IdGeneratorUtils;

String uuid = IdGeneratorUtils.takeUUID();       // 32 位，无连字符
String shortId = IdGeneratorUtils.takeUUID(8);    // 截断 8 位（入参非法/超过 32 返回全量）

String numStr = IdGeneratorUtils.takeRandomNumStr(6);   // 6 位数字字符串
Long num = IdGeneratorUtils.takeRandomNum(18);          // 数字 Long；长度限 [1,18]，超界抛 IllegalArgumentException
String code = IdGeneratorUtils.takeRandomCode(10);      // 10 位字母数字混合

String id = IdGeneratorUtils.takeID(20);         // <=17 位取 UUID 截断；>17 位 = 时间戳17 + 随机补齐
String orderId = IdGeneratorUtils.takeID("ORD-", 16);   // 带前缀：ORD- + 16 位
```

### HttpUtils HTTP 客户端（httpclient5）

底层为静态单例客户端，连接池 100/路由 20，连接与响应超时均 5 秒；GET 参数自动 URL 编码。

```java
import cn.zachqie.wrench.util.HttpUtils;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

// GET
Map<String, Object> params = new HashMap<>();
params.put("kw", "hello world");
JSONObject resp = HttpUtils.doGet("https://api.example.com/search", params);

// POST（JSON body）
Map<String, Object> body = new HashMap<>();
body.put("name", "张三");
Map<String, String> header = new HashMap<>();
header.put("Authorization", "Bearer xxx");
JSONObject resp2 = HttpUtils.doPost("https://api.example.com/users", body, header);

// SSE 流式响应（每行回调一次）
HttpUtils.doGet4Sse("https://api.example.com/stream", params, line -> {
    System.out.println("收到: " + line);
    return null;   // Executor 函数式接口
});
```

**SSL 开关**：默认严格校验证书与主机名。对接自签名证书的内部服务时打开（全局生效，JVM 启动参数）：

```
-Dzachqie.wrench.http.ssl.trust-all=true
```

### ReflectionUtils 反射

```java
import cn.zachqie.wrench.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

Field f = ReflectionUtils.getField(obj, "name");            // 含私有字段，找不到返回 null
Method getter = ReflectionUtils.getGetMethod(obj, "name");   // getName()
Method setter = ReflectionUtils.getSetMethod(obj, "name");   // setName(String)，按字段类型匹配
Object value = getter.invoke(obj);

boolean prim = ReflectionUtils.isPrimitive(String.class);    // 基本类型/包装类/枚举/日期/BigDecimal 等视为简单类型
Field[] marked = ReflectionUtils.getFieldsWithAnnotation(MyClass.class, MyAnnotation.class);
```

### RequestIdUtils 请求 ID（ThreadLocal）

```java
import cn.zachqie.wrench.util.RequestIdUtils;

RequestIdUtils.generateRequestId();       // ThreadLocal 中生成/复用请求 ID
String id = RequestIdUtils.getRequestId();
RequestIdUtils.clearRequestId();           // 请求结束时清理，防止线程池串号
```

### TimeUtils / SpringUtils

```java
import cn.zachqie.wrench.util.TimeUtils;

TimeUtils.sleepMills(500);   // 毫秒 sleep
TimeUtils.sleepSecs(2);       // 秒 sleep

import cn.zachqie.wrench.util.SpringUtils;
MyService svc = SpringUtils.getBean(MyService.class);   // 需要 Spring 容器已启动
```

---

## jwt — 认证

`JwtSupport` 是 `@Component`，需要在 Spring 环境使用。三个配置项（冒号后为默认值，**生产必须覆盖 secret**）：

| 配置 key | 默认 | 说明 |
|---------|------|------|
| `zachqie.wrench.jwt.secret` | 内置值（仅供开发调试） | HS256 签名密钥 |
| `zachqie.wrench.jwt.issuer` | `jwt` | 签发方 |
| `zachqie.wrench.jwt.user_context.accesstoken-expires-sec` | `86400` | token 有效期（秒） |

application.yml 示例：

```yaml
zachqie:
  wrench:
    jwt:
      secret: "生产环境请替换为足够长的随机串"
      issuer: "my-app"
      user_context:
        accesstoken-expires-sec: 7200
```

用法：

```java
import cn.zachqie.wrench.jwt.JwtSupport;
import cn.zachqie.wrench.jwt.UserContext;
import javax.annotation.Resource;

@Resource
private JwtSupport jwtSupport;

// 登录成功后签发
UserContext ctx = new UserContext();
ctx.setUserId("user001");
ctx.setUserName("张三");
ctx.setPlatformCode("PC");
ctx.setAppCode("APP001");
ctx.setUserLevel("VIP");
ctx.setUserPermissions(new HashSet<>(Arrays.asList("read", "write")));
String token = jwtSupport.generateJwtToken(ctx);   // 返回 "Bearer eyJhbGciOi..."，可直接放 Authorization 头

// 网关/拦截器解析
UserContext parsed = jwtSupport.parseJwtToken(token);
// 非 Bearer 前缀、空 token、签名被篡改均抛 cn.zachqie.wrench.base.InvalidRuntimeException
```

---

## ruletree — 规则树（策略路由）

**适用场景**：if/else 分支多、规则会动态变化的业务（如限流策略、路由规则、风控规则）。核心思想：`get()` 负责按条件选出处理器，`router()` 执行它；未命中走默认处理器。

```java
import cn.zachqie.wrench.ruletree.AbstractStrategyRouter;
import cn.zachqie.wrench.ruletree.AbstractMultiThreadStrategyRouter;
import cn.zachqie.wrench.ruletree.StrategyHandler;

// 1. 同步策略路由
public class AmountRouter extends AbstractStrategyRouter<Integer, String, String> {

    @Override
    public StrategyHandler<Integer, String, String> get(Integer amount, String ctx) {
        if (amount >= 1000) {
            return (req, c) -> "MANUAL_REVIEW";     // 命中大额人工审核
        }
        return null;                                 // 未命中 -> 默认处理器
    }

    @Override
    public String apply(Integer req, String ctx) {   // 类本身也是处理器，通常只做兜底
        return "AUTO_PASS";
    }
}

// 使用
String decision = amountRouter.router(5000, "ctx");   // -> "MANUAL_REVIEW"
String fallback = amountRouter.router(100, "ctx");    // 默认处理器（默认返回 null，可 setDefaultStrategyHandler 替换）

// 2. 需要异步预处理的场景：继承 AbstractMultiThreadStrategyRouter，apply() 模板方法会先
//    调 multiThread()（异步准备）再调 doApply()（业务受理）
public class AsyncRouter extends AbstractMultiThreadStrategyRouter<Integer, String, String> {
    @Override
    protected void multiThread(Integer req, String ctx) { /* 异步准备，如查缓存 */ }

    @Override
    protected String doApply(Integer req, String ctx) { return "RESULT"; }

    @Override
    public StrategyHandler<Integer, String, String> get(Integer req, String ctx) { return null; }
}
String result = asyncRouter.apply(1, "ctx");
```

完整示例见 `test-case/src/test/java/cn/zachqie/wrench/test/ruletree/RuleTreeTest.java`。

---

## decisiontree — 决策树

**适用场景**：多节点串行决策流程（如登录风控：设备检查 → 风险评估 → 出账单）。树结构由 VO 组装，节点逻辑实现 `IDecisionTreeNodeLogic`，节点间连线条件实现 `IDecisionTreeNodeLineLogic`。

```java
import cn.zachqie.wrench.decisiontree.DefaultDecisionTreeFactory;
import cn.zachqie.wrench.decisiontree.IDecisionTreeEngine;
import cn.zachqie.wrench.decisiontree.IDecisionTreeNodeLogic;
import cn.zachqie.wrench.decisiontree.valobj.*;
import com.alibaba.fastjson2.JSONObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 1. 定义节点逻辑（每个节点一个类）
public class Logic_LoginCheck implements IDecisionTreeNodeLogic {
    @Override
    public DecisionTreeActionVO logic(String userId, JSONObject inputFactor,
                                       DecisionTreeNodeVO currentVO, DecisionTreeActionVO preAction) {
        boolean pass = inputFactor.getBoolean("pwdOk");
        // 决策结果写入 decisionResultData，连线条件据此路由
        return DecisionTreeActionVO.builder()
                .currentDecisionTreeNode(currentVO.getTreeNodeKey())
                .decisionResultData(pass ? "PASS" : "REJECT")
                .build()
                .appendDecisionPathNodes(preAction);   // 维护路径快照
    }
}

// 2. 组装树
Map<String, IDecisionTreeNodeLogic> nodeLogicMap = new ConcurrentHashMap<>();
nodeLogicMap.put("tree_node_1", new Logic_LoginCheck());
// ... 每个节点注册

DecisionTreeNodeVO node1 = DecisionTreeNodeVO.builder()
        .treeNodeKey("tree_node_1")
        .treeNodeLogic(nodeLogicMap.get("tree_node_1"))
        .build();

DecisionTreeNodeLineVO line = DecisionTreeNodeLineVO.builder()
        .fromTreeNode("tree_node_1")
        .toTreeNode("tree_node_2")
        .ruleLogic((userId, inputFactor, lineVO) -> true)  // 连线条件，实现 IDecisionTreeNodeLineLogic
        .build();

DecisionTreeVO tree = DecisionTreeVO.builder()
        .treeRootKey("tree_node_1")
        .decisionTreeNodeList(Arrays.asList(node1 /*, node2, node3...*/))
        .decisionTreeNodeLineList(Arrays.asList(line /*, ...*/))
        .build();

// 3. 执行
IDecisionTreeEngine engine = new DefaultDecisionTreeFactory().buildDecisionTreeEngine(tree);
JSONObject factor = new JSONObject();
factor.put("pwdOk", true);

java.util.List<DecisionTreeActionVO> actions = engine.process("user001", factor);  // 每一步的动作快照
DecisionTreeActionVO last = engine.process4LastNodeResult("user001", factor);      // 只要最终结果
String path = last.getCurrentDecisionPath();  // 如 "tree_node_1 -> tree_node_2"
```

注意：`decisionResultData` 需要自定义结果 VO 继承 `AbstractDecisionResultDataVO`。完整可运行示例见 `test-case/src/test/java/cn/zachqie/wrench/test/decisiontree/`。

---

## statemachine — 状态机

**适用场景**：单据流转（订单、审批单）。`AbstractStateMachine` 负责流转调度，每个"源状态 → 目标状态"实现一次 `AbstractTransition`。

```java
import cn.zachqie.wrench.statemachine.AbstractStateMachine;
import cn.zachqie.wrench.statemachine.AbstractTransition;
import cn.zachqie.wrench.statemachine.impl.DoNothingTransition;
import cn.zachqie.wrench.statemachine.valobj.StateEventVO;
import cn.zachqie.wrench.statemachine.valobj.StateVO;

// 1. 定义状态
public enum OrderState {
    CREATED, PAID, SHIPPED, COMPLETED
}

// 2. 定义一次流转（如 CREATED -> PAID）
public class Create2PayTransition extends AbstractTransition {
    public Create2PayTransition() {
        super(new StateVO(OrderState.CREATED.name()),
              new StateVO(OrderState.PAID.name()));
    }

    @Override
    public boolean onEvent(StateEventVO event) {
        // 这里做副作用：发消息、记流水等；返回 false 可拦截流转
        return true;
    }
}

// 3. 装配状态机
public class OrderStateMachine extends AbstractStateMachine {
    public OrderStateMachine() {
        StateVO created = new StateVO(OrderState.CREATED.name());
        StateVO paid = new StateVO(OrderState.PAID.name());
        StateVO done = new StateVO(OrderState.COMPLETED.name());

        registerTransition(created, new Create2PayTransition());
        // 未定义的流转用 DoNothingTransition 兜底（不生效、不放行）
        registerTransition(paid, new DoNothingTransition(paid, paid));
        setCurrentState(created);
    }
}

// 4. 触发事件
OrderStateMachine fsm = new OrderStateMachine();
StateEventVO event = new StateEventVO("PAY_EVENT", "ORDER-20261118-001", "ORDER");
boolean moved = fsm.fire(event);          // true = 流转成功
String now = fsm.getCurrentStateName();   // "PAID"
```

完整示例见 `test-case/src/test/java/cn/zachqie/wrench/test/statemachine/`。

---

## test-case — 测试工程

全部模块的单元测试都在这里（JUnit 4），包括各模块 API 的可直接照抄用法：

```
test-case/src/test/java/cn/zachqie/wrench/test/
├── base/BaseTest.java            # Assert / ResponseResult / PageResult / OrderProperty
├── util/UtilTest.java            # DateUtils / IdGeneratorUtils / ReflectionUtils / HttpUtils URL 构建 / RequestIdUtils
├── jwt/JwtTest.java              # token 生成、解析、篡改拒绝（反射注入密钥，脱离 Spring 运行）
├── ruletree/RuleTreeTest.java    # 策略路由、默认处理器、异步模板
├── decisiontree/DecisionTreeTest.java  # 三节点登录决策树完整示例
└── statemachine/StateMachineTest.java   # 订单状态机流转示例
```

运行：`mvn test -pl test-case`（在 `wrench/` 目录下）。

## 构建与发布

```bash
cd wrench
mvn clean install     # 全量构建 + 测试，装入本地仓库
mvn deploy            # 发布到私服（需在 pom 中配置 distributionManagement）
```

## 版本约定

- 坐标：`cn.zachqie.wrench:<module>:1.0.0`
- 修改版本时只需更新父 pom 的 `<version>` 并同步各子模块的 parent 引用。
