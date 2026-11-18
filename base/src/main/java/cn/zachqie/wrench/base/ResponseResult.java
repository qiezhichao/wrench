package cn.zachqie.wrench.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @ClassName ResponseResult
 * @Author qiezhichao
 * @Date 2022/10/26 16:08
 */
@Data
public class ResponseResult<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4818157006403034156L;

    /** 请求成功码 */
    public static final Integer SUCCESS_CODE = 0;
    public static final String DEFAULT_SUCCESS_MESSAGE = "request success";
    /** 请求失败码 */
    public static final Integer FAIl_CODE = -1;
    public static final String DEFAULT_FAILURE_MESSAGE = "request failure";

    /** 响应码, 0:成功, -1:失败 */
    private Integer code;

    /** 响应消息 */
    private String msg;

    /** 响应业务数据 */
    private T data;

    /** 请求id, 用于定位日志问题 */
    private String requestId;

    public static <T> ResponseResult<T> build(Integer code, String msg, T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static <T> ResponseResult<T> build(Integer code, String msg, T data, String requestId) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        result.setRequestId(requestId);
        return result;
    }

    public boolean successful() {
        return Objects.nonNull(this.getCode()) && this.getCode().intValue() == SUCCESS_CODE.intValue();
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    /********************** success **********************/
    public static <T> ResponseResult<T> ok(String msg, T data) {
        return build(SUCCESS_CODE, msg, data);
    }

    public static <T> ResponseResult<T> ok(T data) {
        return ok(DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <T> ResponseResult<T> ok() {
        return ok(null);
    }

    /********************** failure **********************/
    public static <T> ResponseResult<T> fail(String msg, T data) {
        return build(FAIl_CODE, msg, data);
    }

    public static <T> ResponseResult<T> fail(String msg) {
        return fail(msg, null);
    }

    public static <T> ResponseResult<T> fail() {
        return fail(DEFAULT_FAILURE_MESSAGE);
    }
}
