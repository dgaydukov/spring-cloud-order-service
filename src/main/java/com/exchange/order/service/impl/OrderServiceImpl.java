package com.exchange.order.service.impl;

import com.exchange.order.config.ErrorCode;
import com.exchange.order.domain.Asset;
import com.exchange.order.domain.ConvertOrder;
import com.exchange.order.exception.AppException;
import com.exchange.order.facade.AssetFacade;
import com.exchange.order.service.MessageTranslationService;
import com.exchange.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final MessageTranslationService messageTranslationService;
    private final AssetFacade assetFacade;

    private final Map<String, ConvertOrder> orders = new HashMap<>();

    @Override
    public void addOrder(ConvertOrder order) {
        log.info("Adding new order: order={}", order);
        orders.put(order.getSymbol(), order);
    }

    @Override
    public ConvertOrder getOrder(String symbol) {
        log.info("Fetching order: symbol={}", symbol);
        if (!orders.containsKey(symbol)) {
            ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;
            String userMsg = messageTranslationService.getMessage(errorCode.getErrorCode(), new Object[]{symbol});
            throw new AppException(errorCode, userMsg);
        }
        ConvertOrder order = orders.get(symbol);
        Asset asset = assetFacade.getAsset(symbol);
        order.setPrice(asset.getPrice());
        order.setAmount(asset.getPrice() * order.getQuantity());
        log.info("Fetched order: order={}", order);
        return order;
    }
}