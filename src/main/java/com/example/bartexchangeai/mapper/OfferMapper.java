package com.example.bartexchangeai.mapper;

import com.example.bartexchangeai.dto.OfferDto;
import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.model.offer.Category;
import com.example.bartexchangeai.model.group.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для преобразования между Offer и OfferDto
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class, GroupMapper.class})
public interface OfferMapper {
    
    OfferMapper INSTANCE = Mappers.getMapper(OfferMapper.class);
    
    /**
     * Преобразует Offer в OfferDto
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "group.id", target = "groupId")
    OfferDto toDto(Offer offer);
    
    /**
     * Преобразует OfferDto в Offer
     */
    @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "categoryIdToCategory")
    @Mapping(target = "group", source = "groupId", qualifiedByName = "groupIdToGroup")
    @Mapping(target = "exchanges", ignore = true)
    Offer toEntity(OfferDto offerDto);
    
    /**
     * Преобразует список Offer в список OfferDto
     */
    List<OfferDto> toDtoList(List<Offer> offers);
    
    /**
     * Преобразует список OfferDto в список Offer
     */
    List<Offer> toEntityList(List<OfferDto> offerDtos);
    
    /**
     * Преобразует ID пользователя в объект User
     */
    @Named("userIdToUser")
    default User userIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
    
    /**
     * Преобразует ID категории в объект Category
     */
    @Named("categoryIdToCategory")
    default Category categoryIdToCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
    
    /**
     * Преобразует ID группы в объект Group
     */
    @Named("groupIdToGroup")
    default Group groupIdToGroup(Long groupId) {
        if (groupId == null) {
            return null;
        }
        Group group = new Group();
        group.setId(groupId);
        return group;
    }
} 