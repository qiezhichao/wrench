package cn.zachqie.wrench.base;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

public class Assert {

    public static void checkMatch(boolean condition, String errorMessage, Object data) {
        if (!condition) {
            throw new InvalidRuntimeException(errorMessage, data);
        }
    }

    public static void notNull(Object param, String errorMessage) {
        notNull(param, errorMessage, null);
    }

    public static void notNull(Object param, String errorMessage, Object data) {
        Assert.checkMatch(Objects.nonNull(param), errorMessage, data);
    }

    public static void notZero(Integer param, String errorMessage) {
        Assert.checkMatch(Objects.nonNull(param) && param != 0, errorMessage, null);
    }

    public static <T> void notEmpty(Collection<T> param, String errorMessage) {
        notEmpty(param, errorMessage, null);
    }

    public static <T> void notEmpty(Collection<T> param, String errorMessage, Object data) {
        Assert.checkMatch(CollectionUtils.isNotEmpty(param), errorMessage, data);
    }

    public static void notBlank(String param, String errorMessage) {
       notBlank(param, errorMessage, null);
    }

    public static void notBlank(String param, String errorMessage, Object data) {
        Assert.checkMatch(StringUtils.isNotBlank(param), errorMessage, data);
    }

    public static void isTrue(Boolean param, String errorMessage) {
        isTrue(param, errorMessage, null);
    }

    public static void isTrue(Boolean param, String errorMessage, Object data) {
        param = Objects.nonNull(param) && param;
        Assert.checkMatch(param, errorMessage, data);
    }

    public static void isFalse(Boolean param, String errorMessage) {
        isFalse(param, errorMessage, null);
    }

    public static void isFalse(Boolean param, String errorMessage, Object data) {
        param = Objects.nonNull(param) && !param;
        Assert.checkMatch(param, errorMessage, data);
    }

    public static void checkGeTargetNum(BigDecimal param, BigDecimal targetNum, String errorMessage) {
        Assert.checkMatch(
                Objects.isNull(param) || Objects.isNull(targetNum) || param.compareTo(targetNum) >= 0 ,
                errorMessage,
                null);
    }
}
