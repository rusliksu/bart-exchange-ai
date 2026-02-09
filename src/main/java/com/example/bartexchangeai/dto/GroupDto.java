package com.example.bartexchangeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO для работы с группами
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {
    
    private Long id;
    
    @NotBlank(message = "Название группы обязательно")
    private String name;
    
    private String description;
}