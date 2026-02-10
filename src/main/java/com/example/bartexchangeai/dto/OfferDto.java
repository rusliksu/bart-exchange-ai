package com.example.bartexchangeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.bartexchangeai.model.offer.OfferStatus;
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
    
    @NotNull(message = "Статус обязателен")
    private OfferStatus status;
    
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;
    
    @NotNull(message = "ID категории обязателен")
    private Long categoryId;
    
    private Long groupId;
} 