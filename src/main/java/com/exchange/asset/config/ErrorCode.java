package com.exchange.asset.config;

import lombok.Getter;

@Getter
public enum ErrorCode {
    PRICE_NOT_FOUND(10101, "price_not_found");

    private int code;
    private String errorCode;

    ErrorCode(int code, String errorCode) {
        this.code = code;
        this.errorCode = errorCode;
    }
}
