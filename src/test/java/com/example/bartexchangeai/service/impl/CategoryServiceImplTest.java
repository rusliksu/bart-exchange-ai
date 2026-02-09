package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.CategoryDto;
import com.example.bartexchangeai.exception.DuplicateResourceException;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.CategoryMapper;
import com.example.bartexchangeai.model.offer.Category;
import com.example.bartexchangeai.repository.CategoryRepository;
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
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void findAll_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Category category = new Category();
        category.setId(1L);
        category.setName("Электроника");

        CategoryDto categoryDto = new CategoryDto(1L, "Электроника");

        Page<Category> categoryPage = new PageImpl<>(List.of(category));
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        Page<CategoryDto> result = categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Электроника", result.getContent().get(0).getName());
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    void findById_found() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Электроника");

        CategoryDto categoryDto = new CategoryDto(1L, "Электроника");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Электроника", result.getName());
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toDto(category);
    }

    @Test
    void findById_notFound_throws() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.findById(99L));
        verify(categoryRepository).findById(99L);
    }

    @Test
    void findByName_found() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Электроника");

        CategoryDto categoryDto = new CategoryDto(1L, "Электроника");

        when(categoryRepository.findByName("Электроника")).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.findByName("Электроника");

        assertNotNull(result);
        assertEquals("Электроника", result.getName());
        verify(categoryRepository).findByName("Электроника");
    }

    @Test
    void findByName_notFound_throws() {
        when(categoryRepository.findByName("Неизвестная")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.findByName("Неизвестная"));
        verify(categoryRepository).findByName("Неизвестная");
    }

    @Test
    void create_success() {
        CategoryDto inputDto = new CategoryDto(null, "Книги");
        Category category = new Category();
        category.setId(1L);
        category.setName("Книги");

        CategoryDto outputDto = new CategoryDto(1L, "Книги");

        when(categoryRepository.existsByName("Книги")).thenReturn(false);
        when(categoryMapper.toEntity(inputDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(outputDto);

        CategoryDto result = categoryService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Книги", result.getName());
        verify(categoryRepository).existsByName("Книги");
        verify(categoryRepository).save(category);
    }

    @Test
    void create_duplicateName_throws() {
        CategoryDto inputDto = new CategoryDto(null, "Электроника");

        when(categoryRepository.existsByName("Электроника")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoryService.create(inputDto));
        verify(categoryRepository).existsByName("Электроника");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_success() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Старое название");

        CategoryDto updateDto = new CategoryDto(1L, "Новое название");
        CategoryDto outputDto = new CategoryDto(1L, "Новое название");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        when(categoryMapper.toDto(existingCategory)).thenReturn(outputDto);

        CategoryDto result = categoryService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Новое название", result.getName());
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void delete_success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.delete(1L);

        verify(categoryRepository).existsById(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.delete(99L));
        verify(categoryRepository).existsById(99L);
        verify(categoryRepository, never()).deleteById(any());
    }
}
