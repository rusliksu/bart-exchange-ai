package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.RecommendationDto;

import java.util.List;

public interface RecommendationService {

    List<RecommendationDto> getRecommendationsForUser(Long userId);
}
