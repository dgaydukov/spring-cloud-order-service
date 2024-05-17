package com.exchange.asset.service;

import java.util.Locale;

public interface MessageTranslationService {
    String getMessage(String key);

    String getMessage(String key, Object[] params);

    String getMessage(String key, Object[] params, Locale locale);
}
