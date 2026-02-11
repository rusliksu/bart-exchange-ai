package com.example.bartexchangeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для работы с категориями
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    
    private Long id;
    
    @NotBlank(message = "Название категории обязательно")
    @Size(max = 100, message = "Название категории не должно превышать 100 символов")
    private String name;
}