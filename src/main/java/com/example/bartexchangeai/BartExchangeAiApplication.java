package com.example.bartexchangeai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BartExchangeAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BartExchangeAiApplication.class, args);
    }

}
