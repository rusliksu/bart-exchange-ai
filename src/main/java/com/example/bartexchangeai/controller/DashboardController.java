package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DashboardController {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OfferRepository offerRepository;
    private final ExchangeRepository exchangeRepository;
    private final MessageRepository messageRepository;
    private final ReviewRepository reviewRepository;
    private final GroupRepository groupRepository;
    
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("users", userRepository.count());
        dashboard.put("categories", categoryRepository.count());
        dashboard.put("offers", offerRepository.count());
        dashboard.put("exchanges", exchangeRepository.count());
        dashboard.put("messages", messageRepository.count());
        dashboard.put("reviews", reviewRepository.count());
        dashboard.put("groups", groupRepository.count());
        
        dashboard.put("message", "🎉 BartExchangeAI is running successfully!");
        dashboard.put("status", "ACTIVE");
        
        return dashboard;
    }
    
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to BartExchangeAI!");
        response.put("description", "A modern barter exchange platform powered by AI");
        response.put("endpoints", Map.of(
            "users", "/api/users",
            "categories", "/api/categories",
            "offers", "/api/offers",
            "exchanges", "/api/exchanges",
            "dashboard", "/dashboard",
            "h2-console", "/h2-console"
        ));
        response.put("stats", Map.of(
            "users", userRepository.count(),
            "categories", categoryRepository.count(),
            "offers", offerRepository.count(),
            "exchanges", exchangeRepository.count(),
            "messages", messageRepository.count(),
            "reviews", reviewRepository.count(),
            "groups", groupRepository.count()
        ));
        
        return response;
    }
}