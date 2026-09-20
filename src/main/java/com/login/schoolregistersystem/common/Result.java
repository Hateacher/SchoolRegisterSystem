package com.login.schoolregistersystem.common;

import lombok.Data;

/**
 * 统一响应结果（前后端契约：{code, msg, data}）
 * 状态码约定：200 成功；400 业务错误；401 未登录/token失效；500 服务器异常
 */
@Data
public class Result<T> {

    /** 状态码 */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 响应数据 */
    private T data;

    public static <T> Result<T> success(T data) {
        return build(200, "成功", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return build(200, msg, data);
    }

    public static <T> Result<T> error(int code, String msg) {
        return build(code, msg, null);
    }

    /** 业务错误，默认 400 */
    public static <T> Result<T> error(String msg) {
        return build(400, msg, null);
    }

    private static <T> Result<T> build(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
