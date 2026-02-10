package com.example.bartexchangeai.mapper;

import com.example.bartexchangeai.dto.ReviewDto;
import com.example.bartexchangeai.model.user.Review;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.model.exchange.Exchange;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для преобразования между Review и ReviewDto
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, ExchangeMapper.class})
public interface ReviewMapper {
    
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);
    
    /**
     * Преобразует Review в ReviewDto
     */
    @Mapping(source = "reviewer.id", target = "reviewerId")
    @Mapping(source = "reviewedUser.id", target = "reviewedUserId")
    @Mapping(source = "exchange.id", target = "exchangeId")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    ReviewDto toDto(Review review);
    
    /**
     * Преобразует ReviewDto в Review
     */
    @Mapping(target = "reviewer", source = "reviewerId", qualifiedByName = "reviewerIdToUser")
    @Mapping(target = "reviewedUser", source = "reviewedUserId", qualifiedByName = "reviewedUserIdToUser")
    @Mapping(target = "exchange", source = "exchangeId", qualifiedByName = "exchangeIdToExchange")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toEntity(ReviewDto reviewDto);
    
    /**
     * Преобразует список Review в список ReviewDto
     */
    List<ReviewDto> toDtoList(List<Review> reviews);
    
    /**
     * Преобразует список ReviewDto в список Review
     */
    List<Review> toEntityList(List<ReviewDto> reviewDtos);
    
    /**
     * Преобразует ID рецензента в объект User
     */
    @Named("reviewerIdToUser")
    default User reviewerIdToUser(Long reviewerId) {
        if (reviewerId == null) {
            return null;
        }
        User user = new User();
        user.setId(reviewerId);
        return user;
    }
    
    /**
     * Преобразует ID рецензируемого пользователя в объект User
     */
    @Named("reviewedUserIdToUser")
    default User reviewedUserIdToUser(Long reviewedUserId) {
        if (reviewedUserId == null) {
            return null;
        }
        User user = new User();
        user.setId(reviewedUserId);
        return user;
    }
    
    /**
     * Преобразует ID обмена в объект Exchange
     */
    @Named("exchangeIdToExchange")
    default Exchange exchangeIdToExchange(Long exchangeId) {
        if (exchangeId == null) {
            return null;
        }
        Exchange exchange = new Exchange();
        exchange.setId(exchangeId);
        return exchange;
    }
} 