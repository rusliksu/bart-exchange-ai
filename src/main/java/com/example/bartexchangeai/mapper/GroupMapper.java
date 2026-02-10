package com.example.bartexchangeai.mapper;

import com.example.bartexchangeai.dto.GroupDto;
import com.example.bartexchangeai.model.group.Group;
import com.example.bartexchangeai.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для преобразования между Group и GroupDto
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface GroupMapper {
    
    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);
    
    /**
     * Преобразует Group в GroupDto
     */
    GroupDto toDto(Group group);
    
    /**
     * Преобразует GroupDto в Group
     */
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Group toEntity(GroupDto groupDto);
    
    /**
     * Преобразует список Group в список GroupDto
     */
    List<GroupDto> toDtoList(List<Group> groups);
    
    /**
     * Преобразует список GroupDto в список Group
     */
    List<Group> toEntityList(List<GroupDto> groupDtos);
    

} 