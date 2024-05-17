package com.exchange.asset.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppError {

    private int code;
    private String errorCode;
    private String msg;
    private String traceId;
}
