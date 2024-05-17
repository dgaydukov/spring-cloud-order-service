package com.exchange.asset.service;

import com.exchange.asset.domain.ConvertOrder;

public interface OrderService {

    void addOrder(ConvertOrder order);

    ConvertOrder getOrder(String symbol);
}
