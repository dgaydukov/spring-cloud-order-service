package com.exchange.asset.domain;

import lombok.Data;

@Data
public class ConvertOrder {
    private String symbol;
    private double quantity;
    // price per one coin
    private double price;
    // total price of the order
    private double amount;
}
