package com.login.schoolregistersystem.common;

import lombok.Getter;

/**
 * 业务异常：Service 层校验失败时抛出，由 GlobalExceptionHandler 统一转换为 Result
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
