package com.example.bartexchangeai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class SimpleController {

    private final DashboardController dashboardController;

    @GetMapping
    public Map<String, Object> getStatus() {
        return Map.of(
            "status", "BartExchangeAI is running!",
            "message", "Welcome to the barter exchange platform",
            "stats", dashboardController.getStats(),
            "api_endpoints", Map.of(
                "users", "/api/users",
                "categories", "/api/categories",
                "offers", "/api/offers",
                "exchanges", "/api/exchanges",
                "h2_console", "/h2-console"
            )
        );
    }
}