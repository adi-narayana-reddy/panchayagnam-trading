package com.py.common.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);

    @Bean
    public RestTemplate restTemplate() {
        log.info("RestTemplate bean initialized");
        return new RestTemplate();
    }

    @Bean
    public ExecutorService rmsExecutor() {
        log.info("RMS ExecutorService bean initialized with pool size 10");
        return Executors.newFixedThreadPool(10);
    }
}

