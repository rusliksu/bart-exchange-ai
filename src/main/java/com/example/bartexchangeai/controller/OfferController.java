package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.OfferDto;
import com.example.bartexchangeai.model.offer.OfferStatus;
import com.example.bartexchangeai.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Offers", description = "Offer management API")
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    @Operation(summary = "Get all offers")
    public Page<OfferDto> getAllOffers(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return offerService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get offer by ID")
    public OfferDto getOfferById(@PathVariable Long id) {
        return offerService.findById(id);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get offers by status")
    public Page<OfferDto> getOffersByStatus(@PathVariable OfferStatus status, Pageable pageable) {
        return offerService.findByStatus(status, pageable);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get offers by user ID")
    public Page<OfferDto> getOffersByUser(@PathVariable Long userId, Pageable pageable) {
        return offerService.findByUserId(userId, pageable);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get offers by category ID")
    public Page<OfferDto> getOffersByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return offerService.findByCategoryId(categoryId, pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Search offers by keyword")
    public Page<OfferDto> searchOffers(@RequestParam @Size(min = 1, max = 100) String keyword, Pageable pageable) {
        return offerService.search(keyword, pageable);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active offers sorted by newest")
    public Page<OfferDto> getActiveOffers(Pageable pageable) {
        return offerService.findActive(pageable);
    }

    @PostMapping
    @Operation(summary = "Create a new offer")
    public ResponseEntity<OfferDto> createOffer(@Valid @RequestBody OfferDto offerDto) {
        OfferDto created = offerService.create(offerDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing offer")
    @PreAuthorize("@authz.isOfferOwner(#id, authentication.name) or hasRole('ADMIN')")
    public OfferDto updateOffer(@PathVariable Long id, @Valid @RequestBody OfferDto offerDto) {
        return offerService.update(id, offerDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an offer")
    @PreAuthorize("@authz.isOfferOwner(#id, authentication.name) or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
