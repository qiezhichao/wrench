package cn.zachqie.wrench.test.util;

import cn.zachqie.wrench.util.DateUtils;
import cn.zachqie.wrench.util.IdGeneratorUtils;
import cn.zachqie.wrench.util.ReflectionUtils;
import cn.zachqie.wrench.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * util 模块测试：DateUtils、IdGeneratorUtils、ReflectionUtils、TimeUtils、HttpUtils(URL构建)、RequestIdUtils
 */
@Slf4j
public class UtilTest {

    /************************ DateUtils ************************/

    @Test
    public void testDateUtils() {
        // 固定时间戳：2026-11-18 00:00:00 (UTC+8)
        long ts = 1794931200000L;
        Date date = DateUtils.parseDate(ts);
        assertEquals(ts, date.getTime());
        assertNull(DateUtils.parseDate(null));

        String formatted = DateUtils.formatDate(date, DateUtils.DateFormat.yyyy_MM_dd);
        assertTrue(formatted.startsWith("2026-"));
        // null 容错
        assertEquals("", DateUtils.formatDate(null));
        assertEquals("", DateUtils.formatDate(date, null));

        // 一天开始
        Date begin = DateUtils.getDateBegin(date);
        String beginStr = DateUtils.formatDate(begin, DateUtils.DateFormat.yyyy_MM_dd_HH_mm_ss);
        assertTrue(beginStr.endsWith("00:00:00"));

        // 加减
        Date plus1 = DateUtils.plusDays(date, 1);
        assertEquals(ts + 24 * 3600 * 1000L, plus1.getTime());
        Date minus1 = DateUtils.minusDays(date, 1);
        assertEquals(ts - 24 * 3600 * 1000L, minus1.getTime());
        Date plusHours = DateUtils.plusHours(date, 2);
        assertEquals(ts + 2 * 3600 * 1000L, plusHours.getTime());

        // 同天
        assertTrue(DateUtils.isSameDay(date, date));
        assertFalse(DateUtils.isSameDay(date, null));
        assertTrue(DateUtils.isSameDay4Now(new Date()));
    }

    /************************ IdGeneratorUtils ************************/

    @Test
    public void testIdGenerator() {
        String uuid = IdGeneratorUtils.takeUUID();
        assertEquals(32, uuid.length());
        assertFalse(uuid.contains("-"));

        // 定长 UUID 截断
        assertEquals(8, IdGeneratorUtils.takeUUID(8).length());
        // 超长返回完整 UUID
        assertEquals(32, IdGeneratorUtils.takeUUID(50).length());
        // 非法长度返回完整 UUID
        assertEquals(32, IdGeneratorUtils.takeUUID(-1).length());

        // 随机数字字符串
        String numStr = IdGeneratorUtils.takeRandomNumStr(6);
        assertEquals(6, numStr.length());
        assertTrue(numStr.matches("\\d{6}"));

        // 随机数字 Long：18 位内不溢出
        for (int i = 1; i <= 18; i++) {
            long v = IdGeneratorUtils.takeRandomNum(i);
            assertTrue(v >= 0);
        }
        // 超界抛 IllegalArgumentException
        try {
            IdGeneratorUtils.takeRandomNum(19);
            fail("should throw");
        } catch (IllegalArgumentException expected) {
        }
        try {
            IdGeneratorUtils.takeRandomNum(0);
            fail("should throw");
        } catch (IllegalArgumentException expected) {
        }

        // 随机码
        String code = IdGeneratorUtils.takeRandomCode(10);
        assertEquals(10, code.length());
        assertTrue(code.matches("[0-9a-zA-Z]{10}"));

        // ID：17 位以内取 UUID 截断
        assertEquals(10, IdGeneratorUtils.takeID(10).length());
        // 超过 17 位 = 时间戳(17) + 随机
        String id = IdGeneratorUtils.takeID(20);
        assertEquals(20, id.length());
        assertTrue(id.startsWith("20"));

        // 带前缀 ID
        String prefixed = IdGeneratorUtils.takeID("ORD-", 16);
        assertTrue(prefixed.startsWith("ORD-"));
        assertEquals("ORD-".length() + 16, prefixed.length());
    }

    /************************ ReflectionUtils ************************/

    @Test
    public void testReflectionUtils() throws Exception {
        Sample sample = new Sample();

        // 字段查找
        assertNotNull(ReflectionUtils.getField(sample, "name"));
        // 方法查找
        Method getter = ReflectionUtils.getGetMethod(sample, "name");
        assertNotNull(getter);
        assertEquals("tom", getter.invoke(sample));
        Method setter = ReflectionUtils.getSetMethod(sample, "name");
        assertNotNull(setter);
        setter.invoke(sample, "jerry");
        assertEquals("jerry", sample.getName());

        // 基本类型判断
        assertTrue(ReflectionUtils.isPrimitive(String.class));
        assertTrue(ReflectionUtils.isPrimitive(Integer.class));
        assertTrue(ReflectionUtils.isPrimitive(BigDecimal.class));
        assertTrue(ReflectionUtils.isPrimitive(Date.class));
        assertFalse(ReflectionUtils.isPrimitive(Sample.class));

        // getFieldsWithAnnotation
        assertEquals(1, ReflectionUtils.getFieldsWithAnnotation(Sample.class, Deprecated.class).length);
    }

    /************************ HttpUtils URL 构建（不发起真实请求） ************************/

    @Test
    public void testHttpUtilsBuildUrl() throws Exception {
        Class<?> httpUtils = Class.forName("cn.zachqie.wrench.util.HttpUtils");
        java.lang.reflect.Method m = httpUtils.getDeclaredMethod("buildGetMethodURL", String.class, Map.class);
        m.setAccessible(true);

        // 特殊字符与中文编码
        Map<String, Object> params = new HashMap<>();
        params.put("kw", "a b&c=1");
        params.put("name", "张三");
        String url = (String) m.invoke(null, "http://x/api", params);
        assertTrue(url.startsWith("http://x/api?"));
        assertFalse(url.contains("a b"));       // 空格已编码
        assertFalse(url.contains("张三"));       // 中文已编码
        assertFalse(url.contains("&c=1"));      // & 已编码

        // 空参数直接返回原 URL
        assertEquals("http://x/api", m.invoke(null, "http://x/api", new HashMap<>()));
        assertEquals("http://x/api", m.invoke(null, "http://x/api", null));
    }

    /************************ TimeUtils / RequestIdUtils ************************/

    @Test
    public void testTimeAndRequestId() {
        TimeUtils.sleepMills(10);
        TimeUtils.sleepSecs(0);

        cn.zachqie.wrench.util.RequestIdUtils.generateRequestId();
        assertNotNull(cn.zachqie.wrench.util.RequestIdUtils.getRequestId());
        cn.zachqie.wrench.util.RequestIdUtils.clearRequestId();
        assertNull(cn.zachqie.wrench.util.RequestIdUtils.getRequestId());
    }

    /************************ 测试辅助类 ************************/

    public static class Sample {
        private String name = "tom";

        @Deprecated
        private String legacy;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
