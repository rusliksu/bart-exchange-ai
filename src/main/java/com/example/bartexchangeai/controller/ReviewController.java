package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.model.user.Review;
import com.example.bartexchangeai.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewRepository reviewRepository;
    
    @GetMapping
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        return review.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/reviewer/{reviewerId}")
    public List<Review> getReviewsByReviewer(@PathVariable Long reviewerId) {
        return reviewRepository.findByReviewerId(reviewerId);
    }
    
    @GetMapping("/user/{userId}")
    public List<Review> getReviewsForUser(@PathVariable Long userId) {
        return reviewRepository.findByReviewedUserIdOrderByIdDesc(userId);
    }
    
    @GetMapping("/exchange/{exchangeId}")
    public List<Review> getReviewsByExchange(@PathVariable Long exchangeId) {
        return reviewRepository.findByExchangeId(exchangeId);
    }
    
    @GetMapping("/user/{userId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long userId) {
        Double averageRating = reviewRepository.findAverageRatingByReviewedUserId(userId);
        return ResponseEntity.ok(averageRating != null ? averageRating : 0.0);
    }
    
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        // Check if review already exists for this exchange and reviewer
        if (reviewRepository.existsByExchangeIdAndReviewerId(
                review.getExchange().getId(), 
                review.getReviewer().getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        Review savedReview = reviewRepository.save(review);
        return ResponseEntity.ok(savedReview);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review reviewDetails) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setRating(reviewDetails.getRating());
            review.setComment(reviewDetails.getComment());
            Review updatedReview = reviewRepository.save(review);
            return ResponseEntity.ok(updatedReview);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}