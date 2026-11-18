package cn.zachqie.wrench.util;

import java.util.UUID;

/**
 * @ClassName RequestIdUtils
 * @Author qiezhichao
 * @Date 2023/3/7 17:12
 */
public class RequestIdUtils {

    private static final ThreadLocal<UUID> requestIdHolder = new ThreadLocal<>();

    private RequestIdUtils() {}

    public static void generateRequestId() {
        requestIdHolder.set(UUID.randomUUID());
    }

    public static void generateRequestId(UUID uuid) {
        requestIdHolder.set(uuid);
    }

    public static UUID getRequestId() {
        return requestIdHolder.get();
    }

    public static void clearRequestId(){
        requestIdHolder.remove();
    }
}
