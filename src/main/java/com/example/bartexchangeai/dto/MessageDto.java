package com.example.bartexchangeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 5000, message = "Сообщение не должно превышать 5000 символов")
    private String content;
    
    private LocalDateTime timestamp;
    
    @NotNull(message = "ID отправителя обязателен")
    private Long senderId;
    
    @NotNull(message = "ID обмена обязателен")
    private Long exchangeId;
} 