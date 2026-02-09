package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.GroupDto;
import com.example.bartexchangeai.exception.DuplicateResourceException;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.GroupMapper;
import com.example.bartexchangeai.model.group.Group;
import com.example.bartexchangeai.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMapper groupMapper;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    void findAll_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Group group = new Group();
        group.setId(1L);
        group.setName("Электроника");
        group.setDescription("Группа обмена электроникой");

        GroupDto groupDto = new GroupDto(1L, "Электроника", "Группа обмена электроникой");

        Page<Group> groupPage = new PageImpl<>(List.of(group));
        when(groupRepository.findAll(pageable)).thenReturn(groupPage);
        when(groupMapper.toDto(group)).thenReturn(groupDto);

        Page<GroupDto> result = groupService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Электроника", result.getContent().get(0).getName());
        verify(groupRepository).findAll(pageable);
    }

    @Test
    void findById_found() {
        Group group = new Group();
        group.setId(1L);
        group.setName("Электроника");
        group.setDescription("Группа обмена электроникой");

        GroupDto groupDto = new GroupDto(1L, "Электроника", "Группа обмена электроникой");

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(groupMapper.toDto(group)).thenReturn(groupDto);

        GroupDto result = groupService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Электроника", result.getName());
        verify(groupRepository).findById(1L);
        verify(groupMapper).toDto(group);
    }

    @Test
    void findById_notFound_throws() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groupService.findById(99L));
        verify(groupRepository).findById(99L);
    }

    @Test
    void findByName_found() {
        Group group = new Group();
        group.setId(1L);
        group.setName("Книги");
        group.setDescription("Обмен книгами");

        GroupDto groupDto = new GroupDto(1L, "Книги", "Обмен книгами");

        when(groupRepository.findByName("Книги")).thenReturn(Optional.of(group));
        when(groupMapper.toDto(group)).thenReturn(groupDto);

        GroupDto result = groupService.findByName("Книги");

        assertNotNull(result);
        assertEquals("Книги", result.getName());
        verify(groupRepository).findByName("Книги");
    }

    @Test
    void findByName_notFound_throws() {
        when(groupRepository.findByName("Неизвестная")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groupService.findByName("Неизвестная"));
        verify(groupRepository).findByName("Неизвестная");
    }

    @Test
    void create_success() {
        GroupDto inputDto = new GroupDto(null, "Новая группа", "Описание новой группы");
        Group group = new Group();
        group.setId(1L);
        group.setName("Новая группа");
        group.setDescription("Описание новой группы");

        GroupDto outputDto = new GroupDto(1L, "Новая группа", "Описание новой группы");

        when(groupRepository.existsByName("Новая группа")).thenReturn(false);
        when(groupMapper.toEntity(inputDto)).thenReturn(group);
        when(groupRepository.save(group)).thenReturn(group);
        when(groupMapper.toDto(group)).thenReturn(outputDto);

        GroupDto result = groupService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Новая группа", result.getName());
        assertEquals("Описание новой группы", result.getDescription());
        verify(groupRepository).existsByName("Новая группа");
        verify(groupRepository).save(group);
    }

    @Test
    void create_duplicateName_throws() {
        GroupDto inputDto = new GroupDto(null, "Электроника", "Описание");

        when(groupRepository.existsByName("Электроника")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> groupService.create(inputDto));
        verify(groupRepository).existsByName("Электроника");
        verify(groupRepository, never()).save(any());
    }

    @Test
    void update_success() {
        Group existingGroup = new Group();
        existingGroup.setId(1L);
        existingGroup.setName("Старое название");
        existingGroup.setDescription("Старое описание");

        GroupDto updateDto = new GroupDto(1L, "Новое название", "Новое описание");
        GroupDto outputDto = new GroupDto(1L, "Новое название", "Новое описание");

        when(groupRepository.findById(1L)).thenReturn(Optional.of(existingGroup));
        when(groupRepository.save(existingGroup)).thenReturn(existingGroup);
        when(groupMapper.toDto(existingGroup)).thenReturn(outputDto);

        GroupDto result = groupService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Новое название", result.getName());
        assertEquals("Новое описание", result.getDescription());
        verify(groupRepository).findById(1L);
        verify(groupRepository).save(existingGroup);
    }

    @Test
    void update_notFound_throws() {
        GroupDto updateDto = new GroupDto(99L, "Название", "Описание");

        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groupService.update(99L, updateDto));
        verify(groupRepository).findById(99L);
        verify(groupRepository, never()).save(any());
    }

    @Test
    void delete_success() {
        when(groupRepository.existsById(1L)).thenReturn(true);

        groupService.delete(1L);

        verify(groupRepository).existsById(1L);
        verify(groupRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(groupRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> groupService.delete(99L));
        verify(groupRepository).existsById(99L);
        verify(groupRepository, never()).deleteById(any());
    }
}
