package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.MessageDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.service.MessageService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private com.example.bartexchangeai.security.AuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllMessages_returnsPage() throws Exception {
        MessageDto message = new MessageDto(1L, "Привет!", LocalDateTime.now(), 1L, 1L);
        when(messageService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(message)));

        mockMvc.perform(get("/api/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].content").value("Привет!"));
    }

    @Test
    void getMessageById_found() throws Exception {
        MessageDto message = new MessageDto(1L, "Привет!", LocalDateTime.now(), 1L, 1L);
        when(messageService.findById(1L)).thenReturn(message);

        mockMvc.perform(get("/api/messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Привет!"))
                .andExpect(jsonPath("$.senderId").value(1));
    }

    @Test
    void getMessageById_notFound() throws Exception {
        when(messageService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Message", 99L));

        mockMvc.perform(get("/api/messages/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getMessagesByExchange_returnsList() throws Exception {
        MessageDto msg1 = new MessageDto(1L, "Привет!", LocalDateTime.now(), 1L, 5L);
        MessageDto msg2 = new MessageDto(2L, "Здравствуйте!", LocalDateTime.now(), 2L, 5L);
        when(messageService.findByExchangeId(eq(5L))).thenReturn(List.of(msg1, msg2));

        mockMvc.perform(get("/api/messages/exchange/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Привет!"))
                .andExpect(jsonPath("$[1].content").value("Здравствуйте!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMessage_valid() throws Exception {
        MessageDto inputDto = new MessageDto(null, "Новое сообщение", null, 1L, 1L);
        MessageDto savedDto = new MessageDto(1L, "Новое сообщение", LocalDateTime.now(), 1L, 1L);
        when(messageService.create(any(MessageDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Новое сообщение"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMessage_success() throws Exception {
        doNothing().when(messageService).delete(1L);

        mockMvc.perform(delete("/api/messages/1"))
                .andExpect(status().isNoContent());
    }
}
