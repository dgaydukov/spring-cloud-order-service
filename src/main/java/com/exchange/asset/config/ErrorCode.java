package com.exchange.asset.config;

import lombok.Getter;

@Getter
public enum ErrorCode {
    FAILED_TO_PROCESS_REQUEST(101000, "failed_to_process_request"),
    ORDER_NOT_FOUND(101001, "order_not_found");

    private int code;
    private String errorCode;

    ErrorCode(int code, String errorCode) {
        this.code = code;
        this.errorCode = errorCode;
    }
}
