package com.exchange.order.config;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(101000, "server_error"),
    FAILED_TO_PROCESS_REQUEST(101001, "failed_to_process_request"),
    ORDER_NOT_FOUND(101002, "order_not_found");

    private int code;
    private String errorCode;

    ErrorCode(int code, String errorCode) {
        this.code = code;
        this.errorCode = errorCode;
    }
}
