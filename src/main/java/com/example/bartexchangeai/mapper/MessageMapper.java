package com.example.bartexchangeai.mapper;

import com.example.bartexchangeai.dto.MessageDto;
import com.example.bartexchangeai.model.exchange.Message;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.model.exchange.Exchange;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для преобразования между Message и MessageDto
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, ExchangeMapper.class})
public interface MessageMapper {
    
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);
    
    /**
     * Преобразует Message в MessageDto
     */
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "exchange.id", target = "exchangeId")
    MessageDto toDto(Message message);
    
    /**
     * Преобразует MessageDto в Message
     */
    @Mapping(target = "sender", source = "senderId", qualifiedByName = "senderIdToUser")
    @Mapping(target = "exchange", source = "exchangeId", qualifiedByName = "exchangeIdToExchange")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Message toEntity(MessageDto messageDto);
    
    /**
     * Преобразует список Message в список MessageDto
     */
    List<MessageDto> toDtoList(List<Message> messages);
    
    /**
     * Преобразует список MessageDto в список Message
     */
    List<Message> toEntityList(List<MessageDto> messageDtos);
    
    /**
     * Преобразует ID отправителя в объект User
     */
    @Named("senderIdToUser")
    default User senderIdToUser(Long senderId) {
        if (senderId == null) {
            return null;
        }
        User user = new User();
        user.setId(senderId);
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