package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.CategoryDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.service.CategoryService;
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

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private com.example.bartexchangeai.security.AuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllCategories_returnsPage() throws Exception {
        CategoryDto category = new CategoryDto(1L, "Электроника");
        when(categoryService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(category)));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Электроника"));
    }

    @Test
    void getCategoryById_found() throws Exception {
        CategoryDto category = new CategoryDto(1L, "Электроника");
        when(categoryService.findById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Электроника"));
    }

    @Test
    void getCategoryById_notFound() throws Exception {
        when(categoryService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Category", 99L));

        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_valid() throws Exception {
        CategoryDto inputDto = new CategoryDto(null, "Книги");
        CategoryDto savedDto = new CategoryDto(1L, "Книги");
        when(categoryService.create(any(CategoryDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Книги"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_invalid_emptyName() throws Exception {
        CategoryDto invalidDto = new CategoryDto(null, "");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_success() throws Exception {
        doNothing().when(categoryService).delete(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }
}
