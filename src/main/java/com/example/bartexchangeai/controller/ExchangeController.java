package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.ExchangeDto;
import com.example.bartexchangeai.model.exchange.ExchangeStatus;
import com.example.bartexchangeai.service.ExchangeService;
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

@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
@Tag(name = "Exchanges", description = "Exchange management API")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping
    @Operation(summary = "Get all exchanges")
    public Page<ExchangeDto> getAllExchanges(Pageable pageable) {
        return exchangeService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exchange by ID")
    public ExchangeDto getExchangeById(@PathVariable Long id) {
        return exchangeService.findById(id);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get exchanges by status")
    public Page<ExchangeDto> getExchangesByStatus(@PathVariable ExchangeStatus status, Pageable pageable) {
        return exchangeService.findByStatus(status, pageable);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get exchanges by user ID")
    public Page<ExchangeDto> getExchangesByUser(@PathVariable Long userId, Pageable pageable) {
        return exchangeService.findByUserId(userId, pageable);
    }

    @GetMapping("/initiator/{initiatorId}")
    @Operation(summary = "Get exchanges by initiator ID")
    public Page<ExchangeDto> getExchangesByInitiator(@PathVariable Long initiatorId, Pageable pageable) {
        return exchangeService.findByInitiatorId(initiatorId, pageable);
    }

    @GetMapping("/participant/{participantId}")
    @Operation(summary = "Get exchanges by participant ID")
    public Page<ExchangeDto> getExchangesByParticipant(@PathVariable Long participantId, Pageable pageable) {
        return exchangeService.findByParticipantId(participantId, pageable);
    }

    @PostMapping
    @Operation(summary = "Create a new exchange")
    public ResponseEntity<ExchangeDto> createExchange(@Valid @RequestBody ExchangeDto exchangeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exchangeService.create(exchangeDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update exchange status")
    @PreAuthorize("@authz.isExchangeParticipant(#id, authentication.name) or hasRole('ADMIN')")
    public ExchangeDto updateExchange(@PathVariable Long id, @Valid @RequestBody ExchangeDto exchangeDto) {
        return exchangeService.updateStatus(id, exchangeDto.getStatus());
    }

    @PutMapping("/complete/{id}")
    @Operation(summary = "Mark exchange as completed")
    @PreAuthorize("@authz.isExchangeParticipant(#id, authentication.name) or hasRole('ADMIN')")
    public ExchangeDto completeExchange(@PathVariable Long id) {
        return exchangeService.complete(id);
    }

    @PutMapping("/cancel/{id}")
    @Operation(summary = "Cancel an exchange")
    @PreAuthorize("@authz.isExchangeParticipant(#id, authentication.name) or hasRole('ADMIN')")
    public ExchangeDto cancelExchange(@PathVariable Long id) {
        return exchangeService.cancel(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an exchange")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExchange(@PathVariable Long id) {
        exchangeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
