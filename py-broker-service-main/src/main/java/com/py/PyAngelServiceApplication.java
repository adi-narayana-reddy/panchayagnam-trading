package com.py;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PyAngelServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(PyAngelServiceApplication.class);

    public static void main(String[] args) {
        log.info("===== py-broker-service Starting =====");
        SpringApplication.run(PyAngelServiceApplication.class, args);
        log.info("===== py-broker-service Started Successfully =====");
    }
}
