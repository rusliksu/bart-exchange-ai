package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.ExchangeDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.ExchangeMapper;
import com.example.bartexchangeai.model.exchange.Exchange;
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
    public List<ExchangeDto> findByStatus(String status) {
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
        if (!userRepository.existsById(exchangeDto.getInitiatorId())) {
            throw new ResourceNotFoundException("User", exchangeDto.getInitiatorId());
        }
        if (!userRepository.existsById(exchangeDto.getParticipantId())) {
            throw new ResourceNotFoundException("User", exchangeDto.getParticipantId());
        }
        if (!offerRepository.existsById(exchangeDto.getOfferId())) {
            throw new ResourceNotFoundException("Offer", exchangeDto.getOfferId());
        }
        Exchange exchange = exchangeMapper.toEntity(exchangeDto);
        exchange.setDate(LocalDateTime.now());
        if (exchange.getStatus() == null) {
            exchange.setStatus("PENDING");
        }
        return exchangeMapper.toDto(exchangeRepository.save(exchange));
    }

    @Override
    @Transactional
    public ExchangeDto updateStatus(Long id, String status) {
        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange", id));
        exchange.setStatus(status);
        return exchangeMapper.toDto(exchangeRepository.save(exchange));
    }

    @Override
    @Transactional
    public ExchangeDto complete(Long id) {
        return updateStatus(id, "COMPLETED");
    }

    @Override
    @Transactional
    public ExchangeDto cancel(Long id) {
        return updateStatus(id, "CANCELLED");
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
