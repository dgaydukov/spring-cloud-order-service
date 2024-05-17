package com.exchange.asset.service.impl;

import com.exchange.asset.config.AppProps;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigPrinterService {

    private final AppProps appConfig;

    @PostConstruct
    public void print() {
        new Thread(() -> {
            while (true) {
                if (appConfig.isPrintConfig()) {
                    log.info("configEnv={}", appConfig.getConfigEnv());
                }
                sleep(10);
            }
        }).start();
    }

    private void sleep(long sec) {
        try {
            Thread.sleep(1000 * sec);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
