package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.ExchangeDto;
import com.example.bartexchangeai.model.exchange.ExchangeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExchangeService {

    Page<ExchangeDto> findAll(Pageable pageable);

    ExchangeDto findById(Long id);

    List<ExchangeDto> findByStatus(ExchangeStatus status);

    List<ExchangeDto> findByUserId(Long userId);

    List<ExchangeDto> findByInitiatorId(Long initiatorId);

    List<ExchangeDto> findByParticipantId(Long participantId);

    ExchangeDto create(ExchangeDto exchangeDto);

    ExchangeDto updateStatus(Long id, ExchangeStatus status);

    ExchangeDto complete(Long id);

    ExchangeDto cancel(Long id);

    void delete(Long id);
}
