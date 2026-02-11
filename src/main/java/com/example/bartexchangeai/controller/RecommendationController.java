package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.RecommendationDto;
import com.example.bartexchangeai.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations", description = "AI-powered offer recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get personalized recommendations for a user",
            description = "Returns AI-ranked offers based on user's exchange history, categories and ratings")
    public List<RecommendationDto> getRecommendations(@PathVariable Long userId) {
        return recommendationService.getRecommendationsForUser(userId);
    }
}
