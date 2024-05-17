package com.exchange.order.service;

import com.exchange.order.domain.ConvertOrder;

public interface OrderService {

    void addOrder(ConvertOrder order);

    ConvertOrder getOrder(String symbol);
}
