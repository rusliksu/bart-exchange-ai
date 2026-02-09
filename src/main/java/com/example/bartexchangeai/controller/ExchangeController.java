package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.ExchangeDto;
import com.example.bartexchangeai.mapper.ExchangeMapper;
import com.example.bartexchangeai.model.exchange.Exchange;
import com.example.bartexchangeai.repository.ExchangeRepository;
import com.example.bartexchangeai.repository.UserRepository;
import com.example.bartexchangeai.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
public class ExchangeController {
    
    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final ExchangeMapper exchangeMapper;
    
    @GetMapping
    public List<ExchangeDto> getAllExchanges() {
        List<Exchange> exchanges = exchangeRepository.findAll();
        return exchangeMapper.toDtoList(exchanges);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExchangeDto> getExchangeById(@PathVariable Long id) {
        Optional<Exchange> exchange = exchangeRepository.findById(id);
        return exchange.map(ex -> ResponseEntity.ok(exchangeMapper.toDto(ex)))
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public List<ExchangeDto> getExchangesByStatus(@PathVariable String status) {
        List<Exchange> exchanges = exchangeRepository.findByStatus(status);
        return exchangeMapper.toDtoList(exchanges);
    }
    
    @GetMapping("/user/{userId}")
    public List<ExchangeDto> getExchangesByUser(@PathVariable Long userId) {
        List<Exchange> exchanges = exchangeRepository.findByUserId(userId);
        return exchangeMapper.toDtoList(exchanges);
    }
    
    @GetMapping("/initiator/{initiatorId}")
    public List<ExchangeDto> getExchangesByInitiator(@PathVariable Long initiatorId) {
        List<Exchange> exchanges = exchangeRepository.findByInitiatorId(initiatorId);
        return exchangeMapper.toDtoList(exchanges);
    }
    
    @GetMapping("/participant/{participantId}")
    public List<ExchangeDto> getExchangesByParticipant(@PathVariable Long participantId) {
        List<Exchange> exchanges = exchangeRepository.findByParticipantId(participantId);
        return exchangeMapper.toDtoList(exchanges);
    }
    
    @PostMapping
    public ResponseEntity<ExchangeDto> createExchange(@Valid @RequestBody ExchangeDto exchangeDto) {
        if (!userRepository.existsById(exchangeDto.getInitiatorId()) || 
            !userRepository.existsById(exchangeDto.getParticipantId()) ||
            !offerRepository.existsById(exchangeDto.getOfferId())) {
            return ResponseEntity.badRequest().build();
        }
        
        Exchange exchange = exchangeMapper.toEntity(exchangeDto);
        exchange.setDate(LocalDateTime.now());
        if (exchange.getStatus() == null) {
            exchange.setStatus("PENDING");
        }
        
        Exchange savedExchange = exchangeRepository.save(exchange);
        return ResponseEntity.ok(exchangeMapper.toDto(savedExchange));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ExchangeDto> updateExchange(@PathVariable Long id, @Valid @RequestBody ExchangeDto exchangeDetails) {
        Optional<Exchange> optionalExchange = exchangeRepository.findById(id);
        if (optionalExchange.isPresent()) {
            Exchange exchange = optionalExchange.get();
            exchange.setStatus(exchangeDetails.getStatus());
            Exchange updatedExchange = exchangeRepository.save(exchange);
            return ResponseEntity.ok(exchangeMapper.toDto(updatedExchange));
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/complete/{id}")
    public ResponseEntity<ExchangeDto> completeExchange(@PathVariable Long id) {
        Optional<Exchange> optionalExchange = exchangeRepository.findById(id);
        if (optionalExchange.isPresent()) {
            Exchange exchange = optionalExchange.get();
            exchange.setStatus("COMPLETED");
            Exchange updatedExchange = exchangeRepository.save(exchange);
            return ResponseEntity.ok(exchangeMapper.toDto(updatedExchange));
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/cancel/{id}")
    public ResponseEntity<ExchangeDto> cancelExchange(@PathVariable Long id) {
        Optional<Exchange> optionalExchange = exchangeRepository.findById(id);
        if (optionalExchange.isPresent()) {
            Exchange exchange = optionalExchange.get();
            exchange.setStatus("CANCELLED");
            Exchange updatedExchange = exchangeRepository.save(exchange);
            return ResponseEntity.ok(exchangeMapper.toDto(updatedExchange));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExchange(@PathVariable Long id) {
        if (exchangeRepository.existsById(id)) {
            exchangeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}