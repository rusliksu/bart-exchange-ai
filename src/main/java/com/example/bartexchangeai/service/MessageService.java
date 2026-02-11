package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    Page<MessageDto> findAll(Pageable pageable);

    MessageDto findById(Long id);

    Page<MessageDto> findByExchangeId(Long exchangeId, Pageable pageable);

    Page<MessageDto> findBySenderId(Long senderId, Pageable pageable);

    MessageDto create(MessageDto messageDto);

    void delete(Long id);
}
