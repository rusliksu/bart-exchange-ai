package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return Map.of(
            "stats", getStats(),
            "message", "BartExchangeAI is running successfully!",
            "status", "ACTIVE"
        );
    }

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
            "message", "Welcome to BartExchangeAI!",
            "description", "A modern barter exchange platform powered by AI",
            "endpoints", Map.of(
                "users", "/api/users",
                "categories", "/api/categories",
                "offers", "/api/offers",
                "exchanges", "/api/exchanges",
                "dashboard", "/dashboard",
                "h2-console", "/h2-console"
            ),
            "stats", getStats()
        );
    }

    @Cacheable(value = "dashboard_stats")
    public Map<String, Long> getStats() {
        return Map.of(
            "users", userRepository.count(),
            "categories", categoryRepository.count(),
            "offers", offerRepository.count(),
            "exchanges", exchangeRepository.count(),
            "messages", messageRepository.count(),
            "reviews", reviewRepository.count(),
            "groups", groupRepository.count()
        );
    }
}