package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.MessageDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.MessageMapper;
import com.example.bartexchangeai.model.exchange.Message;
import com.example.bartexchangeai.repository.ExchangeRepository;
import com.example.bartexchangeai.repository.MessageRepository;
import com.example.bartexchangeai.repository.UserRepository;
import com.example.bartexchangeai.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ExchangeRepository exchangeRepository;
    private final MessageMapper messageMapper;

    @Override
    public Page<MessageDto> findAll(Pageable pageable) {
        return messageRepository.findAll(pageable).map(messageMapper::toDto);
    }

    @Override
    public MessageDto findById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", id));
        return messageMapper.toDto(message);
    }

    @Override
    public Page<MessageDto> findByExchangeId(Long exchangeId, Pageable pageable) {
        return messageRepository.findPageByExchangeId(exchangeId, pageable).map(messageMapper::toDto);
    }

    @Override
    public Page<MessageDto> findBySenderId(Long senderId, Pageable pageable) {
        return messageRepository.findBySenderId(senderId, pageable).map(messageMapper::toDto);
    }

    @Override
    @Transactional
    public MessageDto create(MessageDto messageDto) {
        if (!userRepository.existsById(messageDto.getSenderId())) {
            throw new ResourceNotFoundException("User", messageDto.getSenderId());
        }
        if (!exchangeRepository.existsById(messageDto.getExchangeId())) {
            throw new ResourceNotFoundException("Exchange", messageDto.getExchangeId());
        }
        Message message = messageMapper.toEntity(messageDto);
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Message", id);
        }
        messageRepository.deleteById(id);
    }
}
