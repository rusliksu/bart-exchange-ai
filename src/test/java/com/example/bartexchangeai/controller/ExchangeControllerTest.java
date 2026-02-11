package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.ExchangeDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.model.exchange.ExchangeStatus;
import com.example.bartexchangeai.service.ExchangeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExchangeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExchangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExchangeService exchangeService;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private com.example.bartexchangeai.security.AuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllExchanges_returnsPage() throws Exception {
        ExchangeDto exchange = new ExchangeDto(1L, ExchangeStatus.PENDING, LocalDateTime.now(), 1L, 2L, 1L);
        when(exchangeService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(exchange)));

        mockMvc.perform(get("/api/exchanges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    void getExchangeById_found() throws Exception {
        ExchangeDto exchange = new ExchangeDto(1L, ExchangeStatus.PENDING, LocalDateTime.now(), 1L, 2L, 1L);
        when(exchangeService.findById(1L)).thenReturn(exchange);

        mockMvc.perform(get("/api/exchanges/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.initiatorId").value(1))
                .andExpect(jsonPath("$.participantId").value(2));
    }

    @Test
    void getExchangeById_notFound() throws Exception {
        when(exchangeService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Exchange", 99L));

        mockMvc.perform(get("/api/exchanges/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createExchange_valid() throws Exception {
        ExchangeDto inputDto = new ExchangeDto(null, null, null, 1L, 2L, 1L);
        ExchangeDto savedDto = new ExchangeDto(1L, ExchangeStatus.PENDING, LocalDateTime.now(), 1L, 2L, 1L);
        when(exchangeService.create(any(ExchangeDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/exchanges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void completeExchange_success() throws Exception {
        ExchangeDto completedDto = new ExchangeDto(1L, ExchangeStatus.COMPLETED, LocalDateTime.now(), 1L, 2L, 1L);
        when(exchangeService.complete(1L)).thenReturn(completedDto);

        mockMvc.perform(put("/api/exchanges/complete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteExchange_success() throws Exception {
        doNothing().when(exchangeService).delete(1L);

        mockMvc.perform(delete("/api/exchanges/1"))
                .andExpect(status().isNoContent());
    }
}
