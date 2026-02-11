package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.OfferDto;
import com.example.bartexchangeai.model.offer.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OfferService {

    Page<OfferDto> findAll(Pageable pageable);

    OfferDto findById(Long id);

    Page<OfferDto> findByStatus(OfferStatus status, Pageable pageable);

    Page<OfferDto> findByUserId(Long userId, Pageable pageable);

    Page<OfferDto> findByCategoryId(Long categoryId, Pageable pageable);

    Page<OfferDto> search(String keyword, Pageable pageable);

    Page<OfferDto> findActive(Pageable pageable);

    OfferDto create(OfferDto offerDto);

    OfferDto update(Long id, OfferDto offerDto);

    void delete(Long id);
}
