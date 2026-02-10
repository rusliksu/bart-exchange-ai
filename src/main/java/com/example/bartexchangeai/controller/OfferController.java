package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.OfferDto;
import com.example.bartexchangeai.model.offer.OfferStatus;
import com.example.bartexchangeai.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Tag(name = "Offers", description = "Offer management API")
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    @Operation(summary = "Get all offers")
    public Page<OfferDto> getAllOffers(Pageable pageable) {
        return offerService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get offer by ID")
    public OfferDto getOfferById(@PathVariable Long id) {
        return offerService.findById(id);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get offers by status")
    public List<OfferDto> getOffersByStatus(@PathVariable OfferStatus status) {
        return offerService.findByStatus(status);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get offers by user ID")
    public List<OfferDto> getOffersByUser(@PathVariable Long userId) {
        return offerService.findByUserId(userId);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get offers by category ID")
    public List<OfferDto> getOffersByCategory(@PathVariable Long categoryId) {
        return offerService.findByCategoryId(categoryId);
    }

    @GetMapping("/search")
    @Operation(summary = "Search offers by keyword")
    public List<OfferDto> searchOffers(@RequestParam String keyword) {
        return offerService.search(keyword);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active offers sorted by newest")
    public List<OfferDto> getActiveOffers() {
        return offerService.findActive();
    }

    @PostMapping
    @Operation(summary = "Create a new offer")
    public OfferDto createOffer(@Valid @RequestBody OfferDto offerDto) {
        return offerService.create(offerDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing offer")
    public OfferDto updateOffer(@PathVariable Long id, @Valid @RequestBody OfferDto offerDto) {
        return offerService.update(id, offerDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an offer")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
