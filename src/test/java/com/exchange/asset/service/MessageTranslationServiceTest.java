package com.exchange.asset.service;

import com.exchange.asset.service.impl.MessageTranslationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class MessageTranslationServiceTest {
    @Test
    public void getMessageTest() {
        MessageSource messageSource = Mockito.mock(MessageSource.class);
        MessageTranslationService service = new MessageTranslationServiceImpl(messageSource);
        final String key = "msg";
        final String userMsg = "hello world";
        Mockito.when(messageSource.getMessage(key, new Object[]{}, LocaleContextHolder.getLocale())).thenReturn(userMsg);
        String translated = service.getMessage(key);
        Assertions.assertEquals(translated, userMsg, "Translation message mismatch");
    }
}
