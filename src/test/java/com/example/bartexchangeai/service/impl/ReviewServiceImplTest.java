package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.ReviewDto;
import com.example.bartexchangeai.exception.DuplicateResourceException;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.ReviewMapper;
import com.example.bartexchangeai.model.user.Review;
import com.example.bartexchangeai.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void findAll_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Review review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setComment("Отличный обмен!");

        ReviewDto reviewDto = new ReviewDto(1L, "Отличный обмен!", 5, LocalDateTime.now(), 1L, 2L, 1L);

        Page<Review> reviewPage = new PageImpl<>(List.of(review));
        when(reviewRepository.findAll(pageable)).thenReturn(reviewPage);
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        Page<ReviewDto> result = reviewService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(5, result.getContent().get(0).getRating());
        verify(reviewRepository).findAll(pageable);
    }

    @Test
    void findById_found() {
        Review review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setComment("Отличный обмен!");

        ReviewDto reviewDto = new ReviewDto(1L, "Отличный обмен!", 5, LocalDateTime.now(), 1L, 2L, 1L);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        ReviewDto result = reviewService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getRating());
        assertEquals("Отличный обмен!", result.getComment());
        verify(reviewRepository).findById(1L);
        verify(reviewMapper).toDto(review);
    }

    @Test
    void findById_notFound_throws() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.findById(99L));
        verify(reviewRepository).findById(99L);
    }

    @Test
    void getAverageRating_returnsValue() {
        when(reviewRepository.findAverageRatingByReviewedUserId(1L)).thenReturn(4.5);

        Double result = reviewService.getAverageRating(1L);

        assertEquals(4.5, result);
        verify(reviewRepository).findAverageRatingByReviewedUserId(1L);
    }

    @Test
    void getAverageRating_nullReturnsZero() {
        when(reviewRepository.findAverageRatingByReviewedUserId(99L)).thenReturn(null);

        Double result = reviewService.getAverageRating(99L);

        assertEquals(0.0, result);
        verify(reviewRepository).findAverageRatingByReviewedUserId(99L);
    }

    @Test
    void create_success() {
        ReviewDto inputDto = new ReviewDto(null, "Хороший обмен", 4, null, 1L, 2L, 3L);
        Review review = new Review();
        review.setId(1L);
        review.setRating(4);
        review.setComment("Хороший обмен");

        ReviewDto outputDto = new ReviewDto(1L, "Хороший обмен", 4, LocalDateTime.now(), 1L, 2L, 3L);

        when(reviewRepository.existsByExchangeIdAndReviewerId(3L, 1L)).thenReturn(false);
        when(reviewMapper.toEntity(inputDto)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDto(review)).thenReturn(outputDto);

        ReviewDto result = reviewService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(4, result.getRating());
        verify(reviewRepository).existsByExchangeIdAndReviewerId(3L, 1L);
        verify(reviewRepository).save(review);
    }

    @Test
    void create_duplicateReview_throws() {
        ReviewDto inputDto = new ReviewDto(null, "Повторный отзыв", 3, null, 1L, 2L, 3L);

        when(reviewRepository.existsByExchangeIdAndReviewerId(3L, 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> reviewService.create(inputDto));
        verify(reviewRepository).existsByExchangeIdAndReviewerId(3L, 1L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void update_success() {
        Review existingReview = new Review();
        existingReview.setId(1L);
        existingReview.setRating(3);
        existingReview.setComment("Старый комментарий");

        ReviewDto updateDto = new ReviewDto(1L, "Обновлённый комментарий", 5, null, 1L, 2L, 3L);
        ReviewDto outputDto = new ReviewDto(1L, "Обновлённый комментарий", 5, LocalDateTime.now(), 1L, 2L, 3L);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(existingReview)).thenReturn(existingReview);
        when(reviewMapper.toDto(existingReview)).thenReturn(outputDto);

        ReviewDto result = reviewService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Обновлённый комментарий", result.getComment());
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).save(existingReview);
    }

    @Test
    void update_notFound_throws() {
        ReviewDto updateDto = new ReviewDto(99L, "Комментарий", 4, null, 1L, 2L, 3L);

        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.update(99L, updateDto));
        verify(reviewRepository).findById(99L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void delete_success() {
        when(reviewRepository.existsById(1L)).thenReturn(true);

        reviewService.delete(1L);

        verify(reviewRepository).existsById(1L);
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(reviewRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> reviewService.delete(99L));
        verify(reviewRepository).existsById(99L);
        verify(reviewRepository, never()).deleteById(any());
    }
}
