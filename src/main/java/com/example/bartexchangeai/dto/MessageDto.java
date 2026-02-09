package com.example.bartexchangeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO для работы с сообщениями
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    
    private Long id;
    
    @NotBlank(message = "Текст сообщения обязателен")
    private String content;
    
    private LocalDateTime timestamp;
    
    @NotNull(message = "ID отправителя обязателен")
    private Long senderId;
    
    @NotNull(message = "ID обмена обязателен")
    private Long exchangeId;
} 