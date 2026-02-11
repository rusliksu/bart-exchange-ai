package com.example.bartexchangeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для работы с группами
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {
    
    private Long id;
    
    @NotBlank(message = "Название группы обязательно")
    @Size(max = 100, message = "Название группы не должно превышать 100 символов")
    private String name;

    @Size(max = 1000, message = "Описание группы не должно превышать 1000 символов")
    private String description;
}