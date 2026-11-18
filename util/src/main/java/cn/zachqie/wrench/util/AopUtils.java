package cn.zachqie.wrench.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;

import java.lang.reflect.Field;

@Slf4j
public class AopUtils {

    public static Object getCglibProxyTargetObject(Object proxy) {
        try {
            Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
            h.setAccessible(true);
            Object dynamicAdvisedInterceptor = h.get(proxy);
            Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            return  ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        } catch (Exception e) {
            log.error("getCglibProxyTargetObject exception, {}", e.getMessage(), e);
            return null;
        }
    }

    public static Object getJdkDynamicProxyTargetObject(Object proxy) {
        try {
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            AopProxy aopProxy = (AopProxy)h.get(proxy);

            Field advised = aopProxy.getClass().getDeclaredField("advised");
            advised.setAccessible(true);

            return  ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
        } catch (Exception e) {
            log.error("getJdkDynamicProxyTargetObject exception, {}", e.getMessage(), e);
            return null;
        }
    }

    public static Object getTarget(Object proxy){
        if (!org.springframework.aop.support.AopUtils.isAopProxy(proxy)) {
            return proxy;
        }

        if (org.springframework.aop.support.AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetObject(proxy);
        } else {
            return getCglibProxyTargetObject(proxy);
        }
    }
}
