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

import java.util.List;

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
    public List<ReviewDto> findByReviewerId(Long reviewerId) {
        return reviewMapper.toDtoList(reviewRepository.findByReviewerId(reviewerId));
    }

    @Override
    public List<ReviewDto> findByReviewedUserId(Long userId) {
        return reviewMapper.toDtoList(reviewRepository.findByReviewedUserIdOrderByIdDesc(userId));
    }

    @Override
    public List<ReviewDto> findByExchangeId(Long exchangeId) {
        return reviewMapper.toDtoList(reviewRepository.findByExchangeId(exchangeId));
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
