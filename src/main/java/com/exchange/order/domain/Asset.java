package com.exchange.order.domain;

import lombok.Data;

@Data
public class Asset {

    private String symbol;
    private double price;
}