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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    public List<ExchangeDto> findByStatus(ExchangeStatus status) {
        return exchangeMapper.toDtoList(exchangeRepository.findByStatus(status));
    }

    @Override
    public List<ExchangeDto> findByUserId(Long userId) {
        return exchangeMapper.toDtoList(exchangeRepository.findByUserId(userId));
    }

    @Override
    public List<ExchangeDto> findByInitiatorId(Long initiatorId) {
        return exchangeMapper.toDtoList(exchangeRepository.findByInitiatorId(initiatorId));
    }

    @Override
    public List<ExchangeDto> findByParticipantId(Long participantId) {
        return exchangeMapper.toDtoList(exchangeRepository.findByParticipantId(participantId));
    }

    @Override
    @Transactional
    public ExchangeDto create(ExchangeDto exchangeDto) {
        if (exchangeDto.getInitiatorId().equals(exchangeDto.getParticipantId())) {
            throw new InvalidOperationException("Initiator and participant cannot be the same user");
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
            throw new InvalidOperationException("Cannot create exchange for non-active offer");
        }
        Exchange exchange = exchangeMapper.toEntity(exchangeDto);
        exchange.setDate(LocalDateTime.now());
        if (exchange.getStatus() == null) {
            exchange.setStatus(ExchangeStatus.PENDING);
        }
        return exchangeMapper.toDto(exchangeRepository.save(exchange));
    }

    @Override
    @Transactional
    public ExchangeDto updateStatus(Long id, ExchangeStatus status) {
        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange", id));
        exchange.setStatus(status);
        return exchangeMapper.toDto(exchangeRepository.save(exchange));
    }

    @Override
    @Transactional
    public ExchangeDto complete(Long id) {
        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange", id));
        if (exchange.getStatus() != ExchangeStatus.PENDING) {
            throw new InvalidOperationException("Can only complete exchanges with PENDING status");
        }
        exchange.setStatus(ExchangeStatus.COMPLETED);
        return exchangeMapper.toDto(exchangeRepository.save(exchange));
    }

    @Override
    @Transactional
    public ExchangeDto cancel(Long id) {
        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange", id));
        if (exchange.getStatus() != ExchangeStatus.PENDING) {
            throw new InvalidOperationException("Can only cancel exchanges with PENDING status");
        }
        exchange.setStatus(ExchangeStatus.CANCELLED);
        return exchangeMapper.toDto(exchangeRepository.save(exchange));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!exchangeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exchange", id);
        }
        exchangeRepository.deleteById(id);
    }
}
