package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.ExchangeDto;
import com.example.bartexchangeai.model.exchange.ExchangeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExchangeService {

    Page<ExchangeDto> findAll(Pageable pageable);

    ExchangeDto findById(Long id);

    Page<ExchangeDto> findByStatus(ExchangeStatus status, Pageable pageable);

    Page<ExchangeDto> findByUserId(Long userId, Pageable pageable);

    Page<ExchangeDto> findByInitiatorId(Long initiatorId, Pageable pageable);

    Page<ExchangeDto> findByParticipantId(Long participantId, Pageable pageable);

    ExchangeDto create(ExchangeDto exchangeDto);

    ExchangeDto updateStatus(Long id, ExchangeStatus status);

    ExchangeDto complete(Long id);

    ExchangeDto cancel(Long id);

    void delete(Long id);
}
