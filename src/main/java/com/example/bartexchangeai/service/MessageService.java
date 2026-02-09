package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {

    Page<MessageDto> findAll(Pageable pageable);

    MessageDto findById(Long id);

    List<MessageDto> findByExchangeId(Long exchangeId);

    List<MessageDto> findBySenderId(Long senderId);

    MessageDto create(MessageDto messageDto);

    void delete(Long id);
}
