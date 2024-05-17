package com.exchange.asset.service;

import com.exchange.asset.domain.ConvertOrder;

public interface OrderService {

    void addOrder(String symbol, double quantity);

    ConvertOrder getOrder(String symbol);
}
