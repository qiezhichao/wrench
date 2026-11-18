package cn.zachqie.wrench.test.base;

import cn.zachqie.wrench.base.Assert;
import cn.zachqie.wrench.base.InvalidRuntimeException;
import cn.zachqie.wrench.base.OrderProperty;
import cn.zachqie.wrench.base.PageProperty;
import cn.zachqie.wrench.base.PageResult;
import cn.zachqie.wrench.base.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * base 模块测试：Assert 断言、ResponseResult、PageResult/PageProperty、OrderProperty
 */
@Slf4j
public class BaseTest {

    /************************ Assert ************************/

    @Test
    public void testAssertNotPass() {
        // 各断言不满足时抛出 InvalidRuntimeException
        assertInvalid(() -> Assert.notNull(null, "param is null"));
        assertInvalid(() -> Assert.notZero(0, "param is zero"));
        assertInvalid(() -> Assert.notZero(null, "param is null"));
        assertInvalid(() -> Assert.notEmpty(Collections.emptyList(), "list is empty"));
        assertInvalid(() -> Assert.notEmpty((List) null, "list is null"));
        assertInvalid(() -> Assert.notBlank(" ", "str is blank"));
        assertInvalid(() -> Assert.isTrue(false, "must be true"));
        assertInvalid(() -> Assert.isTrue(null, "must be true"));
        assertInvalid(() -> Assert.isFalse(true, "must be false"));
        assertInvalid(() -> Assert.isFalse(null, "must be false"));
        assertInvalid(() -> Assert.checkGeTargetNum(new BigDecimal("1"), new BigDecimal("2"), "must ge target"));
        // 数据参数随异常抛出
        try {
            Assert.notNull(null, "param is null", "dataxxx");
            fail("should throw");
        } catch (InvalidRuntimeException e) {
            assertEquals("dataxxx", e.getData());
            assertEquals("param is null", e.getMessage());
        }
    }

    @Test
    public void testAssertPass() {
        // 满足条件时不抛异常
        Assert.notNull(new Object(), "ok");
        Assert.notZero(1, "ok");
        Assert.notEmpty(Arrays.asList("a"), "ok");
        Assert.notBlank("a", "ok");
        Assert.isTrue(true, "ok");
        Assert.isTrue(Boolean.TRUE, "ok");
        Assert.isFalse(false, "ok");
        Assert.checkGeTargetNum(new BigDecimal("2"), new BigDecimal("1"), "ok");
        Assert.checkGeTargetNum(null, new BigDecimal("1"), "ok");
    }

    /************************ ResponseResult ************************/

    @Test
    public void testResponseResult() {
        ResponseResult<String> ok = ResponseResult.ok("success", "data");
        assertTrue(ok.successful());
        assertEquals(Integer.valueOf(0), ok.getCode());
        assertEquals("success", ok.getMsg());
        assertEquals("data", ok.getData());

        ResponseResult<String> fail = ResponseResult.fail("error msg");
        assertFalse(fail.successful());
        assertEquals(Integer.valueOf(-1), fail.getCode());
        assertEquals("error msg", fail.getMsg());
        assertNull(fail.getData());

        ResponseResult<String> withRequestId = ResponseResult.build(0, "msg", "data", "req-1");
        assertEquals("req-1", withRequestId.getRequestId());
        assertTrue(withRequestId.successful());
    }

    /************************ PageResult / PageProperty ************************/

    @Test
    public void testPageProperty() {
        // 默认值：pageNum=1, pageSize=100
        PageProperty def = new PageProperty();
        assertEquals(Integer.valueOf(1), def.getPageNum());
        assertEquals(Integer.valueOf(100), def.getPageSize());

        // 非法值回退默认
        PageProperty bad = new PageProperty(0, -5);
        assertEquals(Integer.valueOf(1), bad.getPageNum());
        assertEquals(Integer.valueOf(100), bad.getPageSize());

        // 超上限截断到 1000
        PageProperty big = new PageProperty(2, 99999);
        assertEquals(Integer.valueOf(1000), big.getPageSize());
    }

    @Test
    public void testPageResult() {
        List<String> list = Arrays.asList("a", "b", "c");
        PageResult<String> page = PageResult.buildPageResult(new PageProperty(1, 2), 3, list);

        assertEquals(1, page.getPageNum());
        assertEquals(2, page.getPageSize());
        assertEquals(3, page.getTotalRows());
        assertEquals(2, page.getTotalPages());
        assertTrue(page.isFirstPage());
        assertFalse(page.isLastPage());
        assertTrue(page.isHasNextPage());
        assertFalse(page.isHasPreviousPage());
        assertFalse(page.isEmptyPageResult());

        // 第二页
        PageResult<String> page2 = PageResult.buildPageResult(2, 2, 3, list);
        assertFalse(page2.isFirstPage());
        assertTrue(page2.isLastPage());

        // pageSize 非法时空结果
        PageResult<String> empty = PageResult.emptyPageResult(0, 0);
        assertTrue(empty.isEmptyPageResult());
        assertTrue(empty.getList().isEmpty());
    }

    /************************ OrderProperty ************************/

    @Test
    public void testOrderProperty() {
        OrderProperty order = new OrderProperty();
        assertFalse(order.hasOrders());

        // 链式构建排序，顺序按添加先后
        order.orderDesc("id").orderAsc("name");
        List<OrderProperty.Order> orders = order.getOrders();
        assertTrue(order.hasOrders());
        assertEquals(2, orders.size());
        assertEquals("id", orders.get(0).getOrderField());
        assertTrue(orders.get(0).getOrderRule() == OrderProperty.OrderRule.DESC);
        assertFalse(orders.get(0).isAsc());
        assertEquals("name", orders.get(1).getOrderField());
        assertTrue(orders.get(1).isAsc());

        // blank 字段被过滤
        OrderProperty withBlank = new OrderProperty();
        withBlank.order(" ", OrderProperty.OrderRule.ASC);
        withBlank.order("age", OrderProperty.OrderRule.DESC);
        assertEquals(1, withBlank.getOrders().size());
        assertEquals("age", withBlank.getOrders().get(0).getOrderField());
    }

    private void assertInvalid(Runnable runnable) {
        try {
            runnable.run();
            fail("should throw InvalidRuntimeException");
        } catch (InvalidRuntimeException expected) {
            // expected
        }
    }
}
