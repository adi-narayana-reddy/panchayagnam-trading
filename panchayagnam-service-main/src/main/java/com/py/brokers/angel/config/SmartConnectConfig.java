package com.py.brokers.angel.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.py.brokers.angel.client.SmartConnect;

@Configuration
public class SmartConnectConfig {

    private static final Logger log = LoggerFactory.getLogger(SmartConnectConfig.class);

    @Value("${smartapi.key:default_key}")
    private String apiKey;

    @Bean
    public SmartConnect smartConnect() {
        log.info("Initializing SmartConnect bean with API key");
        return new SmartConnect(apiKey);
    }
}
