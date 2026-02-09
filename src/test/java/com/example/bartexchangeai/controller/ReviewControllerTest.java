package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.ReviewDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllReviews_returnsPage() throws Exception {
        ReviewDto review = new ReviewDto(1L, "Отличный обмен", 5, LocalDateTime.now(), 1L, 2L, 1L);
        when(reviewService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review)));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].rating").value(5));
    }

    @Test
    void getReviewById_found() throws Exception {
        ReviewDto review = new ReviewDto(1L, "Отличный обмен", 5, LocalDateTime.now(), 1L, 2L, 1L);
        when(reviewService.findById(1L)).thenReturn(review);

        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comment").value("Отличный обмен"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void getReviewById_notFound() throws Exception {
        when(reviewService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Review", 99L));

        mockMvc.perform(get("/api/reviews/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createReview_valid() throws Exception {
        ReviewDto inputDto = new ReviewDto(null, "Хороший обмен", 4, null, 1L, 2L, 1L);
        ReviewDto savedDto = new ReviewDto(1L, "Хороший обмен", 4, LocalDateTime.now(), 1L, 2L, 1L);
        when(reviewService.create(any(ReviewDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    void getAverageRating_returnsValue() throws Exception {
        when(reviewService.getAverageRating(eq(1L))).thenReturn(4.5);

        mockMvc.perform(get("/api/reviews/user/1/average"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(4.5));
    }

    @Test
    void deleteReview_success() throws Exception {
        doNothing().when(reviewService).delete(1L);

        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isNoContent());
    }
}
