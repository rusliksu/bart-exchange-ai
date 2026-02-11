package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.ReviewDto;
import com.example.bartexchangeai.service.ReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review management API")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Get all reviews")
    public Page<ReviewDto> getAllReviews(Pageable pageable) {
        return reviewService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    public ReviewDto getReviewById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @GetMapping("/reviewer/{reviewerId}")
    @Operation(summary = "Get reviews by reviewer ID")
    public Page<ReviewDto> getReviewsByReviewer(@PathVariable Long reviewerId, Pageable pageable) {
        return reviewService.findByReviewerId(reviewerId, pageable);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews for a user")
    public Page<ReviewDto> getReviewsForUser(@PathVariable Long userId, Pageable pageable) {
        return reviewService.findByReviewedUserId(userId, pageable);
    }

    @GetMapping("/exchange/{exchangeId}")
    @Operation(summary = "Get reviews for an exchange")
    public Page<ReviewDto> getReviewsByExchange(@PathVariable Long exchangeId, Pageable pageable) {
        return reviewService.findByExchangeId(exchangeId, pageable);
    }

    @GetMapping("/user/{userId}/average")
    @Operation(summary = "Get average rating for a user")
    public Double getAverageRating(@PathVariable Long userId) {
        return reviewService.getAverageRating(userId);
    }

    @PostMapping
    @Operation(summary = "Create a new review")
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewDto reviewDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(reviewDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a review")
    @PreAuthorize("@authz.isReviewAuthor(#id, authentication.name) or hasRole('ADMIN')")
    public ReviewDto updateReview(@PathVariable Long id, @Valid @RequestBody ReviewDto reviewDto) {
        return reviewService.update(id, reviewDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    @PreAuthorize("@authz.isReviewAuthor(#id, authentication.name) or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
