package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.MessageDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.MessageMapper;
import com.example.bartexchangeai.model.exchange.Message;
import com.example.bartexchangeai.repository.ExchangeRepository;
import com.example.bartexchangeai.repository.MessageRepository;
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
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void findAll_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Message message = new Message();
        message.setId(1L);
        message.setContent("Привет!");
        message.setTimestamp(LocalDateTime.now());

        MessageDto messageDto = new MessageDto(1L, "Привет!", LocalDateTime.now(), 1L, 1L);

        Page<Message> messagePage = new PageImpl<>(List.of(message));
        when(messageRepository.findAll(pageable)).thenReturn(messagePage);
        when(messageMapper.toDto(message)).thenReturn(messageDto);

        Page<MessageDto> result = messageService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Привет!", result.getContent().get(0).getContent());
        verify(messageRepository).findAll(pageable);
    }

    @Test
    void findById_found() {
        Message message = new Message();
        message.setId(1L);
        message.setContent("Привет!");
        message.setTimestamp(LocalDateTime.now());

        MessageDto messageDto = new MessageDto(1L, "Привет!", LocalDateTime.now(), 1L, 1L);

        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(messageMapper.toDto(message)).thenReturn(messageDto);

        MessageDto result = messageService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Привет!", result.getContent());
        verify(messageRepository).findById(1L);
        verify(messageMapper).toDto(message);
    }

    @Test
    void findById_notFound_throws() {
        when(messageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> messageService.findById(99L));
        verify(messageRepository).findById(99L);
    }

    @Test
    void findByExchangeId_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Message message = new Message();
        message.setId(1L);
        message.setContent("Привет!");
        message.setTimestamp(LocalDateTime.now());

        MessageDto messageDto = new MessageDto(1L, "Привет!", LocalDateTime.now(), 1L, 5L);

        Page<Message> messagePage = new PageImpl<>(List.of(message));
        when(messageRepository.findPageByExchangeId(5L, pageable)).thenReturn(messagePage);
        when(messageMapper.toDto(message)).thenReturn(messageDto);

        Page<MessageDto> result = messageService.findByExchangeId(5L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Привет!", result.getContent().get(0).getContent());
        verify(messageRepository).findPageByExchangeId(5L, pageable);
    }

    @Test
    void create_success() {
        MessageDto inputDto = new MessageDto(null, "Новое сообщение", null, 1L, 2L);
        Message message = new Message();
        message.setId(1L);
        message.setContent("Новое сообщение");
        message.setTimestamp(null);

        Message savedMessage = new Message();
        savedMessage.setId(1L);
        savedMessage.setContent("Новое сообщение");
        savedMessage.setTimestamp(LocalDateTime.now());

        MessageDto outputDto = new MessageDto(1L, "Новое сообщение", LocalDateTime.now(), 1L, 2L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(exchangeRepository.existsById(2L)).thenReturn(true);
        when(messageMapper.toEntity(inputDto)).thenReturn(message);
        when(messageRepository.save(message)).thenReturn(savedMessage);
        when(messageMapper.toDto(savedMessage)).thenReturn(outputDto);

        MessageDto result = messageService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Новое сообщение", result.getContent());
        assertNotNull(result.getTimestamp());
        verify(userRepository).existsById(1L);
        verify(exchangeRepository).existsById(2L);
        verify(messageRepository).save(message);
    }

    @Test
    void create_senderNotFound_throws() {
        MessageDto inputDto = new MessageDto(null, "Сообщение", null, 99L, 1L);

        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> messageService.create(inputDto));
        verify(userRepository).existsById(99L);
        verify(messageRepository, never()).save(any());
    }

    @Test
    void create_exchangeNotFound_throws() {
        MessageDto inputDto = new MessageDto(null, "Сообщение", null, 1L, 99L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(exchangeRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> messageService.create(inputDto));
        verify(exchangeRepository).existsById(99L);
        verify(messageRepository, never()).save(any());
    }

    @Test
    void delete_success() {
        when(messageRepository.existsById(1L)).thenReturn(true);

        messageService.delete(1L);

        verify(messageRepository).existsById(1L);
        verify(messageRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(messageRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> messageService.delete(99L));
        verify(messageRepository).existsById(99L);
        verify(messageRepository, never()).deleteById(any());
    }
}
