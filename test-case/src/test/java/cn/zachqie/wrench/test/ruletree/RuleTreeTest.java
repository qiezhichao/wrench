package cn.zachqie.wrench.test.ruletree;

import cn.zachqie.wrench.ruletree.AbstractStrategyRouter;
import cn.zachqie.wrench.ruletree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * ruletree 模块测试：策略路由、默认处理器、异步策略路由
 */
@Slf4j
public class RuleTreeTest {

    /************************ 基础策略路由 ************************/

    @Test
    public void testStrategyRouter() throws Exception {
        // 根据 amount 路由：>=100 走 VIP，否则走 default
        AbstractStrategyRouter<Integer, String, String> router = new AbstractStrategyRouter<Integer, String, String>() {
            @Override
            public StrategyHandler<Integer, String, String> get(Integer amount, String context) {
                if (amount >= 100) {
                    return new StrategyHandler<Integer, String, String>() {
                        @Override
                        public String apply(Integer req, String ctx) {
                            return "VIP_ROUTE:" + req;
                        }
                    };
                }
                return null; // 走 default
            }

            @Override
            public String apply(Integer req, String ctx) {
                return "SELF:" + req; // router 自身也是处理器（本路由不直接使用）
            }
        };

        // 命中自定义策略
        String vip = router.router(200, "ctx");
        assertEquals("VIP_ROUTE:200", vip);

        // 未命中走 DEFAULT（返回 null）
        String def = router.router(50, "ctx");
        assertNull(def);
    }

    @Test
    public void testCustomDefaultHandler() throws Exception {
        AtomicBoolean defaultHit = new AtomicBoolean(false);

        AbstractStrategyRouter<Integer, String, String> router = new AbstractStrategyRouter<Integer, String, String>() {
            @Override
            public StrategyHandler<Integer, String, String> get(Integer amount, String ctx) {
                return null; // 永远命中 default
            }

            @Override
            public String apply(Integer req, String ctx) {
                return "SELF";
            }
        };
        // 替换默认处理器
        router.setDefaultStrategyHandler(new StrategyHandler<Integer, String, String>() {
            @Override
            public String apply(Integer req, String ctx) {
                defaultHit.set(true);
                return "DEFAULT:" + req;
            }
        });

        assertEquals("DEFAULT:7", router.router(7, "ctx"));
        assertTrue(defaultHit.get());
    }

    /************************ 异步策略路由（模板方法） ************************/

    @Test
    public void testMultiThreadStrategyRouter() {
        AtomicBoolean multiThreadCalled = new AtomicBoolean(false);
        AtomicBoolean doApplyCalled = new AtomicBoolean(false);

        cn.zachqie.wrench.ruletree.AbstractMultiThreadStrategyRouter<Integer, String, String> router =
                new cn.zachqie.wrench.ruletree.AbstractMultiThreadStrategyRouter<Integer, String, String>() {
                    @Override
                    protected void multiThread(Integer req, String ctx) {
                        multiThreadCalled.set(true);
                    }

                    @Override
                    protected String doApply(Integer req, String ctx) {
                        doApplyCalled.set(true);
                        return "APPLIED:" + req;
                    }

                    @Override
                    public StrategyHandler<Integer, String, String> get(Integer req, String ctx) {
                        return new StrategyHandler<Integer, String, String>() {
                            @Override
                            public String apply(Integer r, String c) {
                                return "MAPPED:" + r;
                            }
                        };
                    }
                };

        // apply 模板方法：先 multiThread（异步准备），再 doApply（业务受理）
        String result = router.apply(100, "ctx");
        assertEquals("APPLIED:100", result);
        assertTrue(multiThreadCalled.get());
        assertTrue(doApplyCalled.get());

        // router 走映射策略
        assertEquals("MAPPED:1", router.router(1, "ctx"));
    }
}
