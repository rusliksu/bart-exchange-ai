package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.model.exchange.Message;
import com.example.bartexchangeai.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageRepository messageRepository;
    
    @GetMapping
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        Optional<Message> message = messageRepository.findById(id);
        return message.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/exchange/{exchangeId}")
    public List<Message> getMessagesByExchange(@PathVariable Long exchangeId) {
        return messageRepository.findByExchangeIdOrderByTimestampAsc(exchangeId);
    }
    
    @GetMapping("/sender/{senderId}")  
    public List<Message> getMessagesBySender(@PathVariable Long senderId) {
        return messageRepository.findBySenderId(senderId);
    }
    
    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }
        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        if (messageRepository.existsById(id)) {
            messageRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}