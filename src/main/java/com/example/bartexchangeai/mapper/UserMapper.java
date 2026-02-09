package com.example.bartexchangeai.mapper;

import com.example.bartexchangeai.dto.UserDto;
import com.example.bartexchangeai.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для преобразования между User и UserDto
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    /**
     * Преобразует User в UserDto
     */
    UserDto toDto(User user);
    
    /**
     * Преобразует UserDto в User
     */
    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "reviewsGiven", ignore = true)
    @Mapping(target = "reviewsReceived", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "initiatedExchanges", ignore = true)
    @Mapping(target = "participatedExchanges", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserDto userDto);
    
    /**
     * Преобразует список User в список UserDto
     */
    List<UserDto> toDtoList(List<User> users);
    
    /**
     * Преобразует список UserDto в список User
     */
    List<User> toEntityList(List<UserDto> userDtos);
} 