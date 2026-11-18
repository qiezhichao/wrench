package cn.zachqie.wrench.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName TimeUtils
 * @Author qiezhichao
 * @Date 2025/6/9 14:47
 */
@Slf4j
public class TimeUtils {

    public static void sleepMills(long mills) {
        try {
            TimeUnit.MILLISECONDS.sleep(mills);
        } catch (InterruptedException e) {
            log.error("sleepMills exception, {}", e.getMessage(), e);
        }
    }

    public static void sleepSecs(long secs) {
        try {
            TimeUnit.SECONDS.sleep(secs);
        } catch (InterruptedException e) {
            log.error("sleepSecs exception, {}", e.getMessage(), e);
        }
    }
}
