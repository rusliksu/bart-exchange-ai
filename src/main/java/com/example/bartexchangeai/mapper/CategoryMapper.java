package com.example.bartexchangeai.mapper;

import com.example.bartexchangeai.dto.CategoryDto;
import com.example.bartexchangeai.model.offer.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для преобразования между Category и CategoryDto
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
    
    /**
     * Преобразует Category in CategoryDto
     */
    CategoryDto toDto(Category category);
    
    /**
     * Преобразует CategoryDto в Category
     */
    @Mapping(target = "offers", ignore = true)
    Category toEntity(CategoryDto categoryDto);
    
    /**
     * Преобразует список Category в список CategoryDto
     */
    List<CategoryDto> toDtoList(List<Category> categories);
    
    /**
     * Преобразует список CategoryDto в список Category
     */
    @Mapping(target = "offers", ignore = true)
    List<Category> toEntityList(List<CategoryDto> categoryDtos);
}
