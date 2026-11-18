package cn.zachqie.wrench.util;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @ClassName ReflectionUtils
 * @Desc 反射工具类
 * @Author qiezhichao
 * @Date 2025/1/15 10:50
 */

@NoArgsConstructor
public class ReflectionUtils {

    /**
     * @desc 获取obj对象中的属性
     * @author qiezhichao
     * @date 2025/1/21 11:02
     * @param obj 对象
     * @param name 属性名
     * @return Field 属性对象
     */
    public static Field getField(Object obj, String name) {
        return FieldUtils.getField(obj.getClass(), name, true);
    }

    /**
     * @desc 获取obj对象中的方法
     * @author qiezhichao
     * @date 2025/1/21 11:02
     * @param obj 对象
     * @param methodName 方法名
     * @param parameterTypes 参数类型列表
     * @return Method 方法对象
     */
    public static Method getMethod(Object obj, String methodName, Class<?>... parameterTypes) {
        return MethodUtils.getAccessibleMethod(obj.getClass(), methodName, parameterTypes);
    }

    /**
     * @desc 获取obj对象中属性字段的Get方法
     * @author qiezhichao
     * @date 2025/1/21 11:02
     * @param obj 对象
     * @param property 属性字段
     * @return Method 方法对象
     */
    public static Method getGetMethod(Object obj, String property) {
        String methodName = "get" + StringUtils.capitalize(property);
        return getMethod(obj, methodName);
    }

    /**
     * @desc 获取obj对象中属性字段的Set方法
     * @author qiezhichao
     * @date 2025/1/21 11:02
     * @param obj 对象
     * @param property 属性字段
     * @return Method 方法对象
     */
    public static Method getSetMethod(Object obj, String property) {
        String methodName = "set" + StringUtils.capitalize(property);
        // 按字段类型查找 setter；无参匹配只能找到零参方法，而 setter 必然有入参
        Field field = FieldUtils.getField(obj.getClass(), property, true);
        if (field == null) {
            return getMethod(obj, methodName);
        }
        return getMethod(obj, methodName, field.getType());
    }

    /**
     * @desc 获取类对象中标注了注解的属性字段数组
     * @author qiezhichao
     * @date 2025/1/21 11:02
     * @param clazz 类对象
     * @param annClazz 注解类对象
     * @return Field[] 属性字段数组
     */
    public static Field[] getFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annClazz) {
        return FieldUtils.getFieldsWithAnnotation(clazz, annClazz);
    }

    /**
     * @desc 类对象是否基本类型
     * @author qiezhichao
     * @date 2025/1/21 11:02
     * @param clazz 类对象
     * @return boolean 是否基本类型
     */
    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz.isEnum()
                || clazz == Boolean.class
                || clazz == Character.class
                || clazz == Byte.class
                || clazz == Short.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Float.class
                || clazz == Double.class
                || clazz == BigInteger.class
                || clazz == BigDecimal.class
                || clazz == String.class
                || clazz == Date.class
                || clazz == java.sql.Date.class
                || clazz == Time.class
                || clazz == Timestamp.class;
    }
}
