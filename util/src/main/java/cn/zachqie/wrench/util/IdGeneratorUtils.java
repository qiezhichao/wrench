package cn.zachqie.wrench.util;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;


/**
 * @ClassName IdGeneratorUtils
 * @Author qiezhichao
 * @Date 2025/5/19 09:43
 */
public class IdGeneratorUtils {

    private static final String NUMBER_POOL = "0123456789";
    private static final String CAPITAL_LETTER_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SMALL_CASE_LETTER_POOL = "abcdefghijklmnopqrstuvwxyz";
    private static final String ALL_POOL = NUMBER_POOL + CAPITAL_LETTER_POOL + SMALL_CASE_LETTER_POOL;

    private static String generateRandomStr(String src, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0, len = src.length(); i < length; i++) {
            int index = RandomUtils.nextInt(0, len);
            sb.append(src.charAt(index));
        }
        return sb.toString();
    }

    /****************************************************************************************************/

    public static String takeUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String takeUUID(int length) {
        String uuid = takeUUID();
        if (length <= 0) {
            return uuid;
        }
        if (length > uuid.length()) {
            return uuid;
        }

        return uuid.substring(0, length);
    }

    /**
     * @desc 获取随机数字字符串
     * @author qiezhichao
     * @date 2025/5/19 09:49
     * @param length 数字位数
     * @return java.lang.String
     */
    public static String takeRandomNumStr(int length) {
        return generateRandomStr(NUMBER_POOL, length);
    }

    /**
     * @desc 获取随机数字，前导零会因 Long 解析而丢失，仅防溢出
     * @author qiezhichao
     * @date 2025/5/19 09:49
     * @param length 数字位数，范围 [1, 18]
     * @return Long
     */
    public static Long takeRandomNum(int length) {
        if (length < 1 || length > 18) {
            throw new IllegalArgumentException("length must be in [1, 18], current: " + length);
        }
        // 超过 18 位必然超出 Long 值域，解析时抛 NumberFormatException，因此限制入参
        return Long.parseLong(takeRandomNumStr(length));
    }

    public static String takeRandomCode(int length) {
        return generateRandomStr(ALL_POOL, length);
    }

    /**
     * @desc 获取ID字符串
     * @author qiezhichao
     * @date 2025/5/19 09:49
     * @param length 字符串长度
     * @return java.lang.String
     */
    public static String takeID(int length) {
        if (length <= 0) {
            return takeUUID();
        }

        if (length <= 17) {
            return takeUUID(length);
        }

        return formatDate(new Date()) + takeUUID(length - 17);
    }

    public static String takeID(String prefix, int length) {
        return prefix + takeUUID(length);
    }

    private static String formatDate(Date date) {
        if (Objects.isNull(date)) {
            return StringUtils.EMPTY;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(date);
    }
}
