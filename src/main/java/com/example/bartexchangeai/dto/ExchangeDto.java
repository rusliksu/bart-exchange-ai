package com.example.bartexchangeai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO для работы с обменами
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeDto {
    
    private Long id;
    
    private String status;
    
    private LocalDateTime date;
    
    @NotNull(message = "ID инициатора обязателен")
    private Long initiatorId;
    
    @NotNull(message = "ID участника обязателен")
    private Long participantId;
    
    @NotNull(message = "ID предложения обязателен")
    private Long offerId;
} 