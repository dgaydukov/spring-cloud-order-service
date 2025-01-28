package com.exchange.order.service;

import com.exchange.order.domain.Asset;
import com.exchange.order.domain.ConvertOrder;
import com.exchange.order.exception.AppException;
import com.exchange.order.facade.AssetFacade;
import com.exchange.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class OrderServiceTest {
    private static final String SYMBOL = "BTC";

    @Test
    public void getOrderTest(){
        MessageTranslationService messageTranslationService = Mockito.mock(MessageTranslationService.class);
        AssetFacade assetFacade = Mockito.mock(AssetFacade.class);
        OrderService orderService = new OrderServiceImpl(messageTranslationService, assetFacade);
        AppException orderNotFound = Assertions.assertThrows(AppException.class,
                () -> orderService.getOrder(SYMBOL), "Exception should be thrown");
        Assertions.assertEquals(orderNotFound.getMessage(), "order_not_found");

        // adding actual order
        ConvertOrder order = new ConvertOrder();
        order.setSymbol(SYMBOL);
        order.setPrice(100);
        order.setQuantity(1);
        orderService.addOrder(order);

        // check that order can be fetched
        Asset asset = new Asset();
        asset.setSymbol(SYMBOL);
        asset.setPrice(100);
        Mockito.when(assetFacade.getAsset(SYMBOL)).thenReturn(asset);
        ConvertOrder fetchedOrder = orderService.getOrder(SYMBOL);
        Assertions.assertEquals(SYMBOL, fetchedOrder.getSymbol(), "symbol mismatch");
        Assertions.assertEquals(100, fetchedOrder.getPrice(), "price mismatch");
        Assertions.assertEquals(1, fetchedOrder.getQuantity(), "quantity mismatch");
        Assertions.assertEquals(100, fetchedOrder.getAmount(), "amount mismatch");
    }
}
