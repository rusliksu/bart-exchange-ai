package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class SimpleController {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OfferRepository offerRepository;
    private final ExchangeRepository exchangeRepository;
    private final MessageRepository messageRepository;
    private final ReviewRepository reviewRepository;
    private final GroupRepository groupRepository;
    
    @GetMapping
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "✅ BartExchangeAI is running!");
        response.put("message", "Welcome to the barter exchange platform");
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("users", userRepository.count());
        stats.put("categories", categoryRepository.count());
        stats.put("offers", offerRepository.count());
        stats.put("exchanges", exchangeRepository.count());
        stats.put("messages", messageRepository.count());
        stats.put("reviews", reviewRepository.count());
        stats.put("groups", groupRepository.count());
        response.put("stats", stats);
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("users", "http://localhost:8080/api/users");
        endpoints.put("categories", "http://localhost:8080/api/categories");
        endpoints.put("offers", "http://localhost:8080/api/offers");
        endpoints.put("exchanges", "http://localhost:8080/api/exchanges");
        endpoints.put("h2_console", "http://localhost:8080/h2-console");
        response.put("api_endpoints", endpoints);
        
        Map<String, String> h2_settings = new HashMap<>();
        h2_settings.put("jdbc_url", "jdbc:h2:mem:testdb");
        h2_settings.put("username", "sa");
        h2_settings.put("password", "");
        response.put("h2_console_settings", h2_settings);
        
        return response;
    }
}