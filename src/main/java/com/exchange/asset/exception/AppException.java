package com.exchange.asset.exception;

import com.exchange.asset.config.ErrorCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private int code;
    private String errorCode;
    private String msg;

    public AppException(ErrorCode errorCode, String msg) {
        this(errorCode.getCode(), errorCode.getErrorCode(), msg);
    }

    public AppException(int code, String errorCode, String msg) {
        super(errorCode);
        this.code = code;
        this.errorCode = errorCode;
        this.msg = msg;
    }
}