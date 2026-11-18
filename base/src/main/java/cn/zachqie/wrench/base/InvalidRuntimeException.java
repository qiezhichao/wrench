package cn.zachqie.wrench.base;

import lombok.Getter;

/**
 * @ClassName InvalidRuntimeException
 * @Author qiezhichao
 * @Date 2025/4/22 09:55
 */
@Getter
public class InvalidRuntimeException extends RuntimeException {

    private Object data;

    public InvalidRuntimeException(String message) {
        super(message);
    }

    public InvalidRuntimeException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public InvalidRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRuntimeException(Throwable cause) {
        super(cause);
    }
}
