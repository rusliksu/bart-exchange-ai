package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.GroupDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.service.GroupService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private com.example.bartexchangeai.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private com.example.bartexchangeai.security.AuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllGroups_returnsPage() throws Exception {
        GroupDto group = new GroupDto(1L, "Техника", "Группа обмена техникой");
        when(groupService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(group)));

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Техника"));
    }

    @Test
    void getGroupById_found() throws Exception {
        GroupDto group = new GroupDto(1L, "Техника", "Группа обмена техникой");
        when(groupService.findById(1L)).thenReturn(group);

        mockMvc.perform(get("/api/groups/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Техника"))
                .andExpect(jsonPath("$.description").value("Группа обмена техникой"));
    }

    @Test
    void getGroupById_notFound() throws Exception {
        when(groupService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Group", 99L));

        mockMvc.perform(get("/api/groups/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createGroup_valid() throws Exception {
        GroupDto inputDto = new GroupDto(null, "Книги", "Группа обмена книгами");
        GroupDto savedDto = new GroupDto(1L, "Книги", "Группа обмена книгами");
        when(groupService.create(any(GroupDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Книги"));
    }

    @Test
    void searchGroups_returnsResults() throws Exception {
        GroupDto group = new GroupDto(1L, "Техника", "Группа обмена техникой");
        when(groupService.search(eq("техника"))).thenReturn(List.of(group));

        mockMvc.perform(get("/api/groups/search").param("keyword", "техника"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Техника"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteGroup_success() throws Exception {
        doNothing().when(groupService).delete(1L);

        mockMvc.perform(delete("/api/groups/1"))
                .andExpect(status().isNoContent());
    }
}
