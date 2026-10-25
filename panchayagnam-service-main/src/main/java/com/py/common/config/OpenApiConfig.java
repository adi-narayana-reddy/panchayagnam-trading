package com.py.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenApiConfig.class);

    @Bean
    public OpenAPI tradeProOpenAPI() {
        log.info("Initializing OpenAPI documentation bean");
        return new OpenAPI()
                .info(new Info()
                        .title("Panchayagnam Trading Service API")
                        .description("Automated Trading System - Manage demat accounts, trade groups, orders, and market data across Angel One, Motilal Oswal, and Upstox brokers.")
                        .version("v0.0.1")
                        .contact(new Contact().name("Panchayagnam Team"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url("/").description("Default Server")
                ));
    }
}

