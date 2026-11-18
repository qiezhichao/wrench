package cn.zachqie.wrench.util;


import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * @ClassName DateUtils
 * @Author qiezhichao
 * @Date 2022/10/25 13:15
 */
public class DateUtils {

    private static final DateFormat FORMAT_DEFAULT = DateFormat.yyyy_MM_dd_HH_mm_ss;

    public static Date now(){
        return new Date();
    }

    public static String formatDate(Date date, DateFormat format) {
        if (Objects.isNull(date) || Objects.isNull(format)) {
            return StringUtils.EMPTY;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format.getFormat());
        return sdf.format(date);
    }

    public static String formatDate(Date date) {
        return formatDate(date, FORMAT_DEFAULT);
    }

    public static String formatNow(DateFormat format) {
        return formatDate(now(), format);
    }

    public static Date parseDate(Long timestampMill) {
        return Objects.isNull(timestampMill) ? null : new Date(timestampMill);
    }

    public static Date getDateBegin(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }

        // 将 Date 转换为 LocalDate（基于系统默认时区）
        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        // 获取当天的 00:00:00 时间
        LocalDateTime startOfDay = localDate.atStartOfDay();
        // 转换回 Date
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (Objects.isNull(date1) || Objects.isNull(date2)) {
            return false;
        }
        return org.apache.commons.lang3.time.DateUtils.isSameDay(date1, date2);
    }

    public static boolean isSameDay4Now(Date date) {
        return isSameDay(now(), date);
    }

    public static Date minusSeconds(Date date, long seconds) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.minusSeconds(seconds));
    }

    public static Date minusMinutes(Date date, long minutes) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

       return toDate(localDateTime.minusMinutes(minutes));
    }

    public static Date minusHours(Date date, long hours) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.minusHours(hours));
    }

    public static Date minusDays(Date date, long days) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.minusDays(days));
    }


    public static Date minusWeeks(Date date, long weeks) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.minusWeeks(weeks));
    }
    public static Date minusMonths(Date date, long months) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.minusMonths(months));
    }

    public static Date minusYears(Date date, long yeas) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.minusYears(yeas));
    }

    public static Date plusSeconds(Date date, long seconds) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.plusSeconds(seconds));
    }

    public static Date plusMinutes(Date date, long minutes) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.plusMinutes(minutes));
    }

    public static Date plusHours(Date date, long hours) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.plusHours(hours));
    }

    public static Date plusDays(Date date, long days) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.plusDays(days));
    }


    public static Date plusWeeks(Date date, long weeks) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.plusWeeks(weeks));
    }
    public static Date plusMonths(Date date, long months) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.plusMonths(months));
    }

    public static Date plusYears(Date date, long yeas) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        if (Objects.isNull(localDateTime)) {
            return null;
        }

        return toDate(localDateTime.plusYears(yeas));
    }


    /**************************************************************************************************************/
    @Getter
    public enum DateFormat {
        yyyy_MM_dd("yyyy-MM-dd"),
        yyyy_MM_dd_HH_mm_ss("yyyy-MM-dd HH:mm:ss"),
        yyyyMMddHHmmssSSS("yyyyMMddHHmmssSSS"),
        yyyyMMddHHmmss("yyyyMMddHHmmss")
        ;

        private final String format;

        DateFormat(String format) {
            this.format = format;
        }

    }
}
