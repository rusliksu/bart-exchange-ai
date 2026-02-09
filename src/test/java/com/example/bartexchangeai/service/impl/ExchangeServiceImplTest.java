package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.ExchangeDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.ExchangeMapper;
import com.example.bartexchangeai.model.exchange.Exchange;
import com.example.bartexchangeai.repository.ExchangeRepository;
import com.example.bartexchangeai.repository.OfferRepository;
import com.example.bartexchangeai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceImplTest {

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private ExchangeMapper exchangeMapper;

    @InjectMocks
    private ExchangeServiceImpl exchangeService;

    @Test
    void findAll_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Exchange exchange = new Exchange();
        exchange.setId(1L);
        exchange.setStatus("PENDING");
        exchange.setDate(LocalDateTime.now());

        ExchangeDto exchangeDto = new ExchangeDto(1L, "PENDING", LocalDateTime.now(), 1L, 2L, 1L);

        Page<Exchange> exchangePage = new PageImpl<>(List.of(exchange));
        when(exchangeRepository.findAll(pageable)).thenReturn(exchangePage);
        when(exchangeMapper.toDto(exchange)).thenReturn(exchangeDto);

        Page<ExchangeDto> result = exchangeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("PENDING", result.getContent().get(0).getStatus());
        verify(exchangeRepository).findAll(pageable);
    }

    @Test
    void findById_found() {
        Exchange exchange = new Exchange();
        exchange.setId(1L);
        exchange.setStatus("PENDING");
        exchange.setDate(LocalDateTime.now());

        ExchangeDto exchangeDto = new ExchangeDto(1L, "PENDING", LocalDateTime.now(), 1L, 2L, 1L);

        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(exchange));
        when(exchangeMapper.toDto(exchange)).thenReturn(exchangeDto);

        ExchangeDto result = exchangeService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PENDING", result.getStatus());
        verify(exchangeRepository).findById(1L);
        verify(exchangeMapper).toDto(exchange);
    }

    @Test
    void findById_notFound_throws() {
        when(exchangeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> exchangeService.findById(99L));
        verify(exchangeRepository).findById(99L);
    }

    @Test
    void create_success() {
        ExchangeDto inputDto = new ExchangeDto(null, null, null, 1L, 2L, 3L);
        Exchange exchange = new Exchange();
        exchange.setId(1L);
        exchange.setStatus("PENDING");
        exchange.setDate(LocalDateTime.now());

        ExchangeDto outputDto = new ExchangeDto(1L, "PENDING", LocalDateTime.now(), 1L, 2L, 3L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(offerRepository.existsById(3L)).thenReturn(true);
        when(exchangeMapper.toEntity(inputDto)).thenReturn(exchange);
        when(exchangeRepository.save(exchange)).thenReturn(exchange);
        when(exchangeMapper.toDto(exchange)).thenReturn(outputDto);

        ExchangeDto result = exchangeService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PENDING", result.getStatus());
        verify(userRepository).existsById(1L);
        verify(userRepository).existsById(2L);
        verify(offerRepository).existsById(3L);
        verify(exchangeRepository).save(exchange);
    }

    @Test
    void create_initiatorNotFound_throws() {
        ExchangeDto inputDto = new ExchangeDto(null, null, null, 99L, 2L, 3L);

        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> exchangeService.create(inputDto));
        verify(userRepository).existsById(99L);
        verify(exchangeRepository, never()).save(any());
    }

    @Test
    void create_participantNotFound_throws() {
        ExchangeDto inputDto = new ExchangeDto(null, null, null, 1L, 99L, 3L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> exchangeService.create(inputDto));
        verify(exchangeRepository, never()).save(any());
    }

    @Test
    void create_offerNotFound_throws() {
        ExchangeDto inputDto = new ExchangeDto(null, null, null, 1L, 2L, 99L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(offerRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> exchangeService.create(inputDto));
        verify(exchangeRepository, never()).save(any());
    }

    @Test
    void complete_success() {
        Exchange exchange = new Exchange();
        exchange.setId(1L);
        exchange.setStatus("PENDING");
        exchange.setDate(LocalDateTime.now());

        Exchange completedExchange = new Exchange();
        completedExchange.setId(1L);
        completedExchange.setStatus("COMPLETED");
        completedExchange.setDate(exchange.getDate());

        ExchangeDto outputDto = new ExchangeDto(1L, "COMPLETED", LocalDateTime.now(), 1L, 2L, 3L);

        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(exchange));
        when(exchangeRepository.save(exchange)).thenReturn(completedExchange);
        when(exchangeMapper.toDto(completedExchange)).thenReturn(outputDto);

        ExchangeDto result = exchangeService.complete(1L);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(exchangeRepository).findById(1L);
        verify(exchangeRepository).save(exchange);
    }

    @Test
    void cancel_success() {
        Exchange exchange = new Exchange();
        exchange.setId(1L);
        exchange.setStatus("PENDING");
        exchange.setDate(LocalDateTime.now());

        Exchange cancelledExchange = new Exchange();
        cancelledExchange.setId(1L);
        cancelledExchange.setStatus("CANCELLED");
        cancelledExchange.setDate(exchange.getDate());

        ExchangeDto outputDto = new ExchangeDto(1L, "CANCELLED", LocalDateTime.now(), 1L, 2L, 3L);

        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(exchange));
        when(exchangeRepository.save(exchange)).thenReturn(cancelledExchange);
        when(exchangeMapper.toDto(cancelledExchange)).thenReturn(outputDto);

        ExchangeDto result = exchangeService.cancel(1L);

        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
        verify(exchangeRepository).findById(1L);
        verify(exchangeRepository).save(exchange);
    }

    @Test
    void delete_success() {
        when(exchangeRepository.existsById(1L)).thenReturn(true);

        exchangeService.delete(1L);

        verify(exchangeRepository).existsById(1L);
        verify(exchangeRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(exchangeRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> exchangeService.delete(99L));
        verify(exchangeRepository).existsById(99L);
        verify(exchangeRepository, never()).deleteById(any());
    }
}
