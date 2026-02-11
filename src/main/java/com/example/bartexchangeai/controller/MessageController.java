package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.MessageDto;
import com.example.bartexchangeai.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Message management API")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Get all messages")
    public Page<MessageDto> getAllMessages(Pageable pageable) {
        return messageService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get message by ID")
    public MessageDto getMessageById(@PathVariable Long id) {
        return messageService.findById(id);
    }

    @GetMapping("/exchange/{exchangeId}")
    @Operation(summary = "Get messages by exchange ID")
    public List<MessageDto> getMessagesByExchange(@PathVariable Long exchangeId) {
        return messageService.findByExchangeId(exchangeId);
    }

    @GetMapping("/sender/{senderId}")
    @Operation(summary = "Get messages by sender ID")
    public List<MessageDto> getMessagesBySender(@PathVariable Long senderId) {
        return messageService.findBySenderId(senderId);
    }

    @PostMapping
    @Operation(summary = "Send a new message")
    public ResponseEntity<MessageDto> createMessage(@Valid @RequestBody MessageDto messageDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(messageDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a message")
    @PreAuthorize("@authz.isMessageSender(#id, authentication.name) or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
