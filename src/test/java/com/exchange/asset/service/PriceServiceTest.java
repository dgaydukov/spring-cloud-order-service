package com.exchange.asset.service;

import com.exchange.asset.config.ErrorCode;
import com.exchange.asset.exception.AppException;
import com.exchange.asset.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PriceServiceTest {

    private PriceService priceService;

    @BeforeEach
    public void init() {
        MessageTranslationService messageTranslationService = Mockito.mock(MessageTranslationService.class);
        priceService = new PriceServiceImpl(messageTranslationService);
    }

    @Test
    public void setPriceTest() {
        final String symbol = "BTC";
        final double price = 100;
        priceService.setPrice(symbol, price);
        Assertions.assertEquals(price, priceService.getPrice(symbol), "price mismatch");
    }

    @Test
    public void getPriceTest() {
        AppException thrown = Assertions.assertThrows(
                AppException.class,
                () -> priceService.getPrice("BTC"),
                "AppException should be thrown if price not found"
        );
        Assertions.assertEquals(thrown.getMessage(), ErrorCode.PRICE_NOT_FOUND.getErrorCode(),
                "exception message mismatch");
    }
}
