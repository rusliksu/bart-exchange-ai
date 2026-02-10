package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.OfferDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.model.offer.OfferStatus;
import com.example.bartexchangeai.service.OfferService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
@AutoConfigureMockMvc(addFilters = false)
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfferService offerService;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllOffers_returnsPage() throws Exception {
        OfferDto offer = new OfferDto(1L, "Ноутбук", "Хороший ноутбук", OfferStatus.ACTIVE, 1L, 1L, null);
        when(offerService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(offer)));

        mockMvc.perform(get("/api/offers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Ноутбук"));
    }

    @Test
    void getOfferById_found() throws Exception {
        OfferDto offer = new OfferDto(1L, "Ноутбук", "Хороший ноутбук", OfferStatus.ACTIVE, 1L, 1L, null);
        when(offerService.findById(1L)).thenReturn(offer);

        mockMvc.perform(get("/api/offers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Ноутбук"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getOfferById_notFound() throws Exception {
        when(offerService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Offer", 99L));

        mockMvc.perform(get("/api/offers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createOffer_valid() throws Exception {
        OfferDto inputDto = new OfferDto(null, "Телефон", "Новый телефон", OfferStatus.ACTIVE, 1L, 2L, null);
        OfferDto savedDto = new OfferDto(1L, "Телефон", "Новый телефон", OfferStatus.ACTIVE, 1L, 2L, null);
        when(offerService.create(any(OfferDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Телефон"));
    }

    @Test
    void searchOffers_returnsResults() throws Exception {
        OfferDto offer = new OfferDto(1L, "Ноутбук Lenovo", "Игровой ноутбук", OfferStatus.ACTIVE, 1L, 1L, null);
        when(offerService.search(eq("ноутбук"))).thenReturn(List.of(offer));

        mockMvc.perform(get("/api/offers/search").param("keyword", "ноутбук"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Ноутбук Lenovo"));
    }

    @Test
    void deleteOffer_success() throws Exception {
        doNothing().when(offerService).delete(1L);

        mockMvc.perform(delete("/api/offers/1"))
                .andExpect(status().isNoContent());
    }
}
