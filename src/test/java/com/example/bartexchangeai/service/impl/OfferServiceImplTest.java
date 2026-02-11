package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.OfferDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.OfferMapper;
import com.example.bartexchangeai.model.offer.Category;
import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.offer.OfferStatus;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.repository.CategoryRepository;
import com.example.bartexchangeai.repository.OfferRepository;
import com.example.bartexchangeai.repository.UserRepository;
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
class OfferServiceImplTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OfferMapper offerMapper;

    @InjectMocks
    private OfferServiceImpl offerService;

    @Test
    void findAll_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Offer offer = new Offer();
        offer.setId(1L);
        offer.setTitle("Ноутбук");
        offer.setStatus(OfferStatus.ACTIVE);

        OfferDto offerDto = new OfferDto(1L, "Ноутбук", "Хороший ноутбук", OfferStatus.ACTIVE, 1L, 1L, null);

        Page<Offer> offerPage = new PageImpl<>(List.of(offer));
        when(offerRepository.findAll(pageable)).thenReturn(offerPage);
        when(offerMapper.toDto(offer)).thenReturn(offerDto);

        Page<OfferDto> result = offerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Ноутбук", result.getContent().get(0).getTitle());
        verify(offerRepository).findAll(pageable);
    }

    @Test
    void findById_found() {
        Offer offer = new Offer();
        offer.setId(1L);
        offer.setTitle("Ноутбук");

        OfferDto offerDto = new OfferDto(1L, "Ноутбук", "Хороший ноутбук", OfferStatus.ACTIVE, 1L, 1L, null);

        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(offerMapper.toDto(offer)).thenReturn(offerDto);

        OfferDto result = offerService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ноутбук", result.getTitle());
        verify(offerRepository).findById(1L);
        verify(offerMapper).toDto(offer);
    }

    @Test
    void findById_notFound_throws() {
        when(offerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> offerService.findById(99L));
        verify(offerRepository).findById(99L);
    }

    @Test
    void findByStatus_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Offer offer = new Offer();
        offer.setId(1L);
        offer.setTitle("Ноутбук");
        offer.setStatus(OfferStatus.ACTIVE);

        OfferDto offerDto = new OfferDto(1L, "Ноутбук", "Описание", OfferStatus.ACTIVE, 1L, 1L, null);

        Page<Offer> offerPage = new PageImpl<>(List.of(offer));
        when(offerRepository.findByStatus(OfferStatus.ACTIVE, pageable)).thenReturn(offerPage);
        when(offerMapper.toDto(offer)).thenReturn(offerDto);

        Page<OfferDto> result = offerService.findByStatus(OfferStatus.ACTIVE, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(OfferStatus.ACTIVE, result.getContent().get(0).getStatus());
        verify(offerRepository).findByStatus(OfferStatus.ACTIVE, pageable);
    }

    @Test
    void create_success() {
        OfferDto inputDto = new OfferDto(null, "Ноутбук", "Хороший ноутбук", OfferStatus.ACTIVE, 1L, 2L, null);
        Offer offer = new Offer();
        offer.setId(1L);
        offer.setTitle("Ноутбук");
        offer.setDescription("Хороший ноутбук");
        offer.setStatus(OfferStatus.ACTIVE);

        OfferDto outputDto = new OfferDto(1L, "Ноутбук", "Хороший ноутбук", OfferStatus.ACTIVE, 1L, 2L, null);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.existsById(2L)).thenReturn(true);
        when(offerMapper.toEntity(inputDto)).thenReturn(offer);
        when(offerRepository.save(offer)).thenReturn(offer);
        when(offerMapper.toDto(offer)).thenReturn(outputDto);

        OfferDto result = offerService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ноутбук", result.getTitle());
        verify(userRepository).existsById(1L);
        verify(categoryRepository).existsById(2L);
        verify(offerRepository).save(offer);
    }

    @Test
    void create_userNotFound_throws() {
        OfferDto inputDto = new OfferDto(null, "Ноутбук", "Описание", OfferStatus.ACTIVE, 99L, 1L, null);

        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> offerService.create(inputDto));
        verify(userRepository).existsById(99L);
        verify(offerRepository, never()).save(any());
    }

    @Test
    void create_categoryNotFound_throws() {
        OfferDto inputDto = new OfferDto(null, "Ноутбук", "Описание", OfferStatus.ACTIVE, 1L, 99L, null);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> offerService.create(inputDto));
        verify(categoryRepository).existsById(99L);
        verify(offerRepository, never()).save(any());
    }

    @Test
    void update_success() {
        Offer existingOffer = new Offer();
        existingOffer.setId(1L);
        existingOffer.setTitle("Старый заголовок");
        existingOffer.setDescription("Старое описание");
        existingOffer.setStatus(OfferStatus.ACTIVE);

        Category category = new Category();
        category.setId(2L);
        category.setName("Электроника");

        OfferDto updateDto = new OfferDto(1L, "Новый заголовок", "Новое описание", OfferStatus.CANCELLED, 1L, 2L, null);
        OfferDto outputDto = new OfferDto(1L, "Новый заголовок", "Новое описание", OfferStatus.CANCELLED, 1L, 2L, null);

        when(offerRepository.findById(1L)).thenReturn(Optional.of(existingOffer));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(offerRepository.save(existingOffer)).thenReturn(existingOffer);
        when(offerMapper.toDto(existingOffer)).thenReturn(outputDto);

        OfferDto result = offerService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Новый заголовок", result.getTitle());
        assertEquals("Новое описание", result.getDescription());
        assertEquals(OfferStatus.CANCELLED, result.getStatus());
        verify(offerRepository).findById(1L);
        verify(offerRepository).save(existingOffer);
    }

    @Test
    void delete_success() {
        when(offerRepository.existsById(1L)).thenReturn(true);

        offerService.delete(1L);

        verify(offerRepository).existsById(1L);
        verify(offerRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(offerRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> offerService.delete(99L));
        verify(offerRepository).existsById(99L);
        verify(offerRepository, never()).deleteById(any());
    }
}
