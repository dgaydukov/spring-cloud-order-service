package com.exchange.asset.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Slf4j
@RefreshScope
public class AppProps {

    @Value("${app.config.env:dev}")
    private String configEnv;

    @Value("${app.config.print:true}")
    private boolean printConfig;
}