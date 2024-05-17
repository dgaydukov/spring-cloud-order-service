package com.exchange.asset.service.impl;

import com.exchange.asset.config.Constants;
import com.exchange.asset.service.MessageTranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageTranslationServiceImpl implements MessageTranslationService {
    private final MessageSource messageSource;

    @Override
    public String getMessage(String key) {
        return getMessage(key, Constants.EMPTY_PARAMS);
    }

    @Override
    public String getMessage(String key, Object[] params) {
        return getMessage(key, params, LocaleContextHolder.getLocale());
    }

    public String getMessage(String key, Object[] params, Locale locale) {
        try{
            return messageSource.getMessage(key, params, locale);
        } catch (NoSuchMessageException ex){
            log.error("Failed to find translation: key={}, params={}, locale={}", key, Arrays.toString(params), locale);
            return null;
        }
    }
}