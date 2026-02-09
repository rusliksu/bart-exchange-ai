package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.ReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    Page<ReviewDto> findAll(Pageable pageable);

    ReviewDto findById(Long id);

    List<ReviewDto> findByReviewerId(Long reviewerId);

    List<ReviewDto> findByReviewedUserId(Long userId);

    List<ReviewDto> findByExchangeId(Long exchangeId);

    Double getAverageRating(Long userId);

    ReviewDto create(ReviewDto reviewDto);

    ReviewDto update(Long id, ReviewDto reviewDto);

    void delete(Long id);
}
