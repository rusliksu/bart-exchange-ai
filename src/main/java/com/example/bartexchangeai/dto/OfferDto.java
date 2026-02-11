package com.example.bartexchangeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.bartexchangeai.model.offer.OfferStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO для работы с предложениями
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferDto {
    
    private Long id;
    
    @NotBlank(message = "Заголовок обязателен")
    @Size(max = 200, message = "Заголовок не должен превышать 200 символов")
    private String title;

    @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
    private String description;
    
    @NotNull(message = "Статус обязателен")
    private OfferStatus status;
    
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;
    
    @NotNull(message = "ID категории обязателен")
    private Long categoryId;
    
    private Long groupId;
} 