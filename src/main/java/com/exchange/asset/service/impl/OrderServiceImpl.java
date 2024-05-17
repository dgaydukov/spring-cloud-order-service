package com.exchange.asset.service.impl;

import com.exchange.asset.config.ErrorCode;
import com.exchange.asset.domain.Asset;
import com.exchange.asset.domain.ConvertOrder;
import com.exchange.asset.exception.AppException;
import com.exchange.asset.facade.AssetFacade;
import com.exchange.asset.service.MessageTranslationService;
import com.exchange.asset.service.OrderService;
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

    private Map<String, ConvertOrder> orders = new HashMap<>();

    @Override
    public void addOrder(ConvertOrder order) {
        log.info("Adding new order: order={}", order);
        orders.put(order.getSymbol(), order);
    }

    @Override
    public ConvertOrder getOrder(String symbol) {
        log.info("Fetching order: symbol={}", symbol);
//        if (!orders.containsKey(symbol)) {
//            ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;
//            String userMsg = messageTranslationService.getMessage(errorCode.getErrorCode(), new Object[]{symbol});
//            throw new AppException(errorCode, userMsg);
//        }
        Asset asset = assetFacade.getAsset(symbol);
        System.out.println(asset);
        ConvertOrder order = orders.get(symbol);
        log.info("Fetched order: order={}", order);
        return order;
    }
}