package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.OfferDto;
import com.example.bartexchangeai.model.offer.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OfferService {

    Page<OfferDto> findAll(Pageable pageable);

    OfferDto findById(Long id);

    List<OfferDto> findByStatus(OfferStatus status);

    List<OfferDto> findByUserId(Long userId);

    List<OfferDto> findByCategoryId(Long categoryId);

    List<OfferDto> search(String keyword);

    List<OfferDto> findActive();

    OfferDto create(OfferDto offerDto);

    OfferDto update(Long id, OfferDto offerDto);

    void delete(Long id);
}
