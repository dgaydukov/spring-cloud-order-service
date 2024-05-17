package com.exchange.asset.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppError {

    private int code;
    private String errorCode;
    private String msg;
    private String traceId;
}
