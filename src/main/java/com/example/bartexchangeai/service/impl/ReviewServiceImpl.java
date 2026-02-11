package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.ReviewDto;
import com.example.bartexchangeai.exception.DuplicateResourceException;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.ReviewMapper;
import com.example.bartexchangeai.model.user.Review;
import com.example.bartexchangeai.repository.ReviewRepository;
import com.example.bartexchangeai.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public Page<ReviewDto> findAll(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(reviewMapper::toDto);
    }

    @Override
    public ReviewDto findById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));
        return reviewMapper.toDto(review);
    }

    @Override
    public Page<ReviewDto> findByReviewerId(Long reviewerId, Pageable pageable) {
        return reviewRepository.findByReviewerId(reviewerId, pageable).map(reviewMapper::toDto);
    }

    @Override
    public Page<ReviewDto> findByReviewedUserId(Long userId, Pageable pageable) {
        return reviewRepository.findByReviewedUserId(userId, pageable).map(reviewMapper::toDto);
    }

    @Override
    public Page<ReviewDto> findByExchangeId(Long exchangeId, Pageable pageable) {
        return reviewRepository.findByExchangeId(exchangeId, pageable).map(reviewMapper::toDto);
    }

    @Override
    public Double getAverageRating(Long userId) {
        Double avg = reviewRepository.findAverageRatingByReviewedUserId(userId);
        return avg != null ? avg : 0.0;
    }

    @Override
    @Transactional
    public ReviewDto create(ReviewDto reviewDto) {
        if (reviewDto.getExchangeId() != null && reviewDto.getReviewerId() != null
                && reviewRepository.existsByExchangeIdAndReviewerId(reviewDto.getExchangeId(), reviewDto.getReviewerId())) {
            throw new DuplicateResourceException("Отзыв от данного пользователя для этого обмена уже существует");
        }
        Review review = reviewMapper.toEntity(reviewDto);
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewDto update(Long id, ReviewDto reviewDto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review", id);
        }
        reviewRepository.deleteById(id);
    }
}
