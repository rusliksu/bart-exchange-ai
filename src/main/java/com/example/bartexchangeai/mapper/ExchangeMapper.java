package com.example.bartexchangeai.mapper;

import com.example.bartexchangeai.dto.ExchangeDto;
import com.example.bartexchangeai.model.exchange.Exchange;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.model.offer.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для преобразования между Exchange и ExchangeDto
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, OfferMapper.class})
public interface ExchangeMapper {
    
    ExchangeMapper INSTANCE = Mappers.getMapper(ExchangeMapper.class);
    
    /**
     * Преобразует Exchange в ExchangeDto
     */
    @Mapping(source = "initiator.id", target = "initiatorId")
    @Mapping(source = "participant.id", target = "participantId")
    @Mapping(source = "offer.id", target = "offerId")
    ExchangeDto toDto(Exchange exchange);
    
    /**
     * Преобразует ExchangeDto в Exchange
     */
    @Mapping(target = "initiator", source = "initiatorId", qualifiedByName = "initiatorIdToUser")
    @Mapping(target = "participant", source = "participantId", qualifiedByName = "participantIdToUser")
    @Mapping(target = "offer", source = "offerId", qualifiedByName = "offerIdToOffer")
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Exchange toEntity(ExchangeDto exchangeDto);
    
    /**
     * Преобразует список Exchange в список ExchangeDto
     */
    List<ExchangeDto> toDtoList(List<Exchange> exchanges);
    
    /**
     * Преобразует список ExchangeDto в список Exchange
     */
    List<Exchange> toEntityList(List<ExchangeDto> exchangeDtos);
    
    /**
     * Преобразует ID инициатора в объект User
     */
    @Named("initiatorIdToUser")
    default User initiatorIdToUser(Long initiatorId) {
        if (initiatorId == null) {
            return null;
        }
        User user = new User();
        user.setId(initiatorId);
        return user;
    }
    
    /**
     * Преобразует ID участника в объект User
     */
    @Named("participantIdToUser")
    default User participantIdToUser(Long participantId) {
        if (participantId == null) {
            return null;
        }
        User user = new User();
        user.setId(participantId);
        return user;
    }
    
    /**
     * Преобразует ID предложения в объект Offer
     */
    @Named("offerIdToOffer")
    default Offer offerIdToOffer(Long offerId) {
        if (offerId == null) {
            return null;
        }
        Offer offer = new Offer();
        offer.setId(offerId);
        return offer;
    }
} 