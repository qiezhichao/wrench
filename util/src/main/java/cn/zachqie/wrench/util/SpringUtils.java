package cn.zachqie.wrench.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName SpringUtils
 * @Author qiezhichao
 * @Date 2022/10/25 13:49
 */
@Component
public class SpringUtils implements ApplicationContextAware, EnvironmentAware {

    private static ApplicationContext applicationContext;
    private static Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> Map<String, T> getBeans(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    public static <T> T getBean(Class<T> clazz, String beanName) {
        return applicationContext.getBean(beanName, clazz);
    }

    @Override
    public void setEnvironment(Environment environment) {
        SpringUtils.environment = environment;
    }

    public static <T> T getValue(String key, Class<T> clazz) {
        return environment.getProperty(key, clazz);
    }
}
