package com.exchange.asset.service.impl;

import com.exchange.asset.config.ErrorCode;
import com.exchange.asset.domain.AppError;
import com.exchange.asset.domain.Asset;
import com.exchange.asset.domain.ConvertOrder;
import com.exchange.asset.exception.AppException;
import com.exchange.asset.facade.AssetFacade;
import com.exchange.asset.service.MessageTranslationService;
import com.exchange.asset.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
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
        if (!orders.containsKey(symbol)) {
            ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;
            String userMsg = messageTranslationService.getMessage(errorCode.getErrorCode(), new Object[]{symbol});
            throw new AppException(errorCode, userMsg);
        }
        Asset asset;
        try{
            asset = assetFacade.getAsset(symbol);
        } catch (FeignException ex){
            String errorResponseBody = ex.contentUTF8();
            ObjectMapper mapper = new ObjectMapper();
            try{
                AppError appError = mapper.readValue(errorResponseBody, AppError.class);
                log.error("Failed to fetch price: appError={}", appError);
            } catch (JsonProcessingException ex2){
                log.error("Failed to parse response body: body={}", errorResponseBody);
            }
            ErrorCode errorCode = ErrorCode.FAILED_TO_PROCESS_REQUEST;
            String userMsg = messageTranslationService.getMessage(errorCode.getErrorCode());
            throw new AppException(errorCode, userMsg);
        }
        ConvertOrder order = orders.get(symbol);
        order.setPrice(asset.getPrice());
        order.setAmount(asset.getPrice()*order.getQuantity());
        log.info("Fetched order: order={}", order);
        return order;
    }
}