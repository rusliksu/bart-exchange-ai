package com.example.bartexchangeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDateTime;

/**
 * DTO для работы с отзывами
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    
    private Long id;
    
    private String comment;
    
    @NotNull(message = "Рейтинг обязателен")
    @Min(value = 1, message = "Рейтинг должен быть от 1 до 5")
    @Max(value = 5, message = "Рейтинг должен быть от 1 до 5")
    private Integer rating;
    
    private LocalDateTime timestamp;
    
    @NotNull(message = "ID рецензента обязателен")
    private Long reviewerId;
    
    @NotNull(message = "ID рецензируемого пользователя обязателен")
    private Long reviewedUserId;
    
    private Long exchangeId;
} 