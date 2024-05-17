package com.exchange.asset.service.impl;

import com.exchange.asset.service.MessageTranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageTranslationServiceImpl implements MessageTranslationService {
    private final MessageSource messageSource;

    @Override
    public String getMessage(String key) {
        return getMessage(key, new Object[]{});
    }

    @Override
    public String getMessage(String key, Object[] params) {
        return getMessage(key, params, LocaleContextHolder.getLocale());
    }

    public String getMessage(String key, Object[] params, Locale locale) {
        return messageSource.getMessage(key, params, locale);
    }
}
