package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.UserDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.service.UserService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private com.example.bartexchangeai.security.AuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsers_returnsPage() throws Exception {
        UserDto user = new UserDto(1L, "testuser", "test@example.com", 4.5f);
        when(userService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    void getUserById_found() throws Exception {
        UserDto user = new UserDto(1L, "testuser", "test@example.com", 4.5f);
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserById_notFound() throws Exception {
        when(userService.findById(99L))
                .thenThrow(new ResourceNotFoundException("User", 99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_valid() throws Exception {
        UserDto inputDto = new UserDto(null, "newuser", "new@example.com", null);
        UserDto savedDto = new UserDto(1L, "newuser", "new@example.com", 0.0f);
        when(userService.create(any(UserDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_invalid_emptyUsername() throws Exception {
        UserDto invalidDto = new UserDto(null, "", "test@example.com", null);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_success() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
