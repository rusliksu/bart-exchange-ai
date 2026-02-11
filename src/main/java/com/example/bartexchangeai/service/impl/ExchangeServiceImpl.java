package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.ExchangeDto;
import com.example.bartexchangeai.exception.InvalidOperationException;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.ExchangeMapper;
import com.example.bartexchangeai.model.exchange.Exchange;
import com.example.bartexchangeai.model.exchange.ExchangeStatus;
import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.offer.OfferStatus;
import com.example.bartexchangeai.repository.ExchangeRepository;
import com.example.bartexchangeai.repository.OfferRepository;
import com.example.bartexchangeai.repository.UserRepository;
import com.example.bartexchangeai.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final ExchangeMapper exchangeMapper;

    @Override
    public Page<ExchangeDto> findAll(Pageable pageable) {
        return exchangeRepository.findAll(pageable).map(exchangeMapper::toDto);
    }

    @Override
    public ExchangeDto findById(Long id) {
        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange", id));
        return exchangeMapper.toDto(exchange);
    }

    @Override
    public Page<ExchangeDto> findByStatus(ExchangeStatus status, Pageable pageable) {
        return exchangeRepository.findByStatus(status, pageable).map(exchangeMapper::toDto);
    }

    @Override
    public Page<ExchangeDto> findByUserId(Long userId, Pageable pageable) {
        return exchangeRepository.findPageByUserId(userId, pageable).map(exchangeMapper::toDto);
    }

    @Override
    public Page<ExchangeDto> findByInitiatorId(Long initiatorId, Pageable pageable) {
        return exchangeRepository.findByInitiatorId(initiatorId, pageable).map(exchangeMapper::toDto);
    }

    @Override
    public Page<ExchangeDto> findByParticipantId(Long participantId, Pageable pageable) {
        return exchangeRepository.findByParticipantId(participantId, pageable).map(exchangeMapper::toDto);
    }

    @Override
    @Transactional
    public ExchangeDto create(ExchangeDto exchangeDto) {
        if (exchangeDto.getInitiatorId().equals(exchangeDto.getParticipantId())) {
            throw new InvalidOperationException("Инициатор и участник не могут быть одним пользователем");
        }
        if (!userRepository.existsById(exchangeDto.getInitiatorId())) {
            throw new ResourceNotFoundException("User", exchangeDto.getInitiatorId());
        }
        if (!userRepository.existsById(exchangeDto.getParticipantId())) {
            throw new ResourceNotFoundException("User", exchangeDto.getParticipantId());
        }
        Offer offer = offerRepository.findById(exchangeDto.getOfferId())
                .orElseThrow(() -> new ResourceNotFoundException("Offer", exchangeDto.getOfferId()));
        if (offer.getStatus() != OfferStatus.ACTIVE) {
            throw new InvalidOperationException("Нельзя создать обмен для неактивного предложения");
        }
        Exchange exchange = exchangeMapper.toEntity(exchangeDto);
        exchange.setDate(LocalDateTime.now());
        if (exchange.getStatus() == null) {
            exchange.setStatus(ExchangeStatus.PENDING);
        }
        Exchange saved = exchangeRepository.save(exchange);
        log.info("Exchange created: id={}, initiator={}, participant={}, offer={}",
                saved.getId(), exchangeDto.getInitiatorId(), exchangeDto.getParticipantId(), exchangeDto.getOfferId());
        return exchangeMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ExchangeDto updateStatus(Long id, ExchangeStatus status) {
        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange", id));
        exchange.setStatus(status);
        log.info("Exchange status updated: id={}, status={}", id, status);
        return exchangeMapper.toDto(exchangeRepository.save(exchange));
    }

    @Override
    @Transactional
    public ExchangeDto complete(Long id) {
        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange", id));
        if (exchange.getStatus() != ExchangeStatus.PENDING) {
            throw new InvalidOperationException("Завершить можно только обмен в статусе PENDING");
        }
        exchange.setStatus(ExchangeStatus.COMPLETED);
        log.info("Exchange completed: id={}", id);
        return exchangeMapper.toDto(exchangeRepository.save(exchange));
    }

    @Override
    @Transactional
    public ExchangeDto cancel(Long id) {
        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange", id));
        if (exchange.getStatus() != ExchangeStatus.PENDING) {
            throw new InvalidOperationException("Отменить можно только обмен в статусе PENDING");
        }
        exchange.setStatus(ExchangeStatus.CANCELLED);
        log.info("Exchange cancelled: id={}", id);
        return exchangeMapper.toDto(exchangeRepository.save(exchange));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!exchangeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exchange", id);
        }
        exchangeRepository.deleteById(id);
        log.warn("Exchange deleted: id={}", id);
    }
}
