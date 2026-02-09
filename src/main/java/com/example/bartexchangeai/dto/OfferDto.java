package com.example.bartexchangeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO для работы с предложениями
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferDto {
    
    private Long id;
    
    @NotBlank(message = "Заголовок обязателен")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Статус обязателен")
    private String status;
    
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;
    
    @NotNull(message = "ID категории обязателен")
    private Long categoryId;
    
    private Long groupId;
} 