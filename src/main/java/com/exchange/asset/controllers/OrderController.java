package com.exchange.asset.controllers;

import com.exchange.asset.domain.ConvertOrder;
import com.exchange.asset.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public void addOrder(@RequestBody ConvertOrder order) {
        orderService.addOrder(order.getSymbol(), order.getQuantity());
    }

    @GetMapping("/{symbol}")
    public ConvertOrder getOrder(@PathVariable String symbol) {
        return orderService.getOrder(symbol);
    }
}