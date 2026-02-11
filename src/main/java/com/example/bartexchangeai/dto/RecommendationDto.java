package com.example.bartexchangeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для AI-рекомендаций офферов
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {

    private Long offerId;

    private String offerTitle;

    private String categoryName;

    private Double score;

    private String reason;
}
