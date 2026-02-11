package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.ReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    Page<ReviewDto> findAll(Pageable pageable);

    ReviewDto findById(Long id);

    Page<ReviewDto> findByReviewerId(Long reviewerId, Pageable pageable);

    Page<ReviewDto> findByReviewedUserId(Long userId, Pageable pageable);

    Page<ReviewDto> findByExchangeId(Long exchangeId, Pageable pageable);

    Double getAverageRating(Long userId);

    ReviewDto create(ReviewDto reviewDto);

    ReviewDto update(Long id, ReviewDto reviewDto);

    void delete(Long id);
}
