package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.RecommendationDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationService recommendationService;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private com.example.bartexchangeai.security.AuthorizationService authorizationService;

    @Test
    void getRecommendations_success() throws Exception {
        List<RecommendationDto> recommendations = List.of(
                new RecommendationDto(10L, "Guitar", "Music", 0.95, "Matches your interests"),
                new RecommendationDto(20L, "Book", "Education", 0.80, "Popular in your category")
        );
        when(recommendationService.getRecommendationsForUser(1L)).thenReturn(recommendations);

        mockMvc.perform(get("/api/recommendations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].offerId").value(10))
                .andExpect(jsonPath("$[0].offerTitle").value("Guitar"))
                .andExpect(jsonPath("$[0].score").value(0.95))
                .andExpect(jsonPath("$[0].reason").value("Matches your interests"))
                .andExpect(jsonPath("$[1].offerId").value(20));
    }

    @Test
    void getRecommendations_userNotFound() throws Exception {
        when(recommendationService.getRecommendationsForUser(99L))
                .thenThrow(new ResourceNotFoundException("User", 99L));

        mockMvc.perform(get("/api/recommendations/user/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
