package com.exchange.order.controllers;

import com.exchange.order.domain.ConvertOrder;
import com.exchange.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public void addOrder(@RequestBody ConvertOrder order) {
        orderService.addOrder(order);
    }

    @GetMapping("/{symbol}")
    public ConvertOrder getOrder(@PathVariable String symbol) {
        return orderService.getOrder(symbol);
    }
}