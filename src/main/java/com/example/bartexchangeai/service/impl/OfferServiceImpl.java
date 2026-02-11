package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.OfferDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.OfferMapper;
import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.offer.OfferStatus;
import com.example.bartexchangeai.repository.CategoryRepository;
import com.example.bartexchangeai.repository.OfferRepository;
import com.example.bartexchangeai.repository.UserRepository;
import com.example.bartexchangeai.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OfferMapper offerMapper;

    @Override
    public Page<OfferDto> findAll(Pageable pageable) {
        return offerRepository.findAll(pageable).map(offerMapper::toDto);
    }

    @Override
    public OfferDto findById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer", id));
        return offerMapper.toDto(offer);
    }

    @Override
    public Page<OfferDto> findByStatus(OfferStatus status, Pageable pageable) {
        return offerRepository.findByStatus(status, pageable).map(offerMapper::toDto);
    }

    @Override
    public Page<OfferDto> findByUserId(Long userId, Pageable pageable) {
        return offerRepository.findByUserId(userId, pageable).map(offerMapper::toDto);
    }

    @Override
    public Page<OfferDto> findByCategoryId(Long categoryId, Pageable pageable) {
        return offerRepository.findByCategoryId(categoryId, pageable).map(offerMapper::toDto);
    }

    @Override
    public Page<OfferDto> search(String keyword, Pageable pageable) {
        return offerRepository.searchByKeyword(keyword, pageable).map(offerMapper::toDto);
    }

    @Override
    public Page<OfferDto> findActive(Pageable pageable) {
        return offerRepository.findActiveOffers(pageable).map(offerMapper::toDto);
    }

    @Override
    @Transactional
    public OfferDto create(OfferDto offerDto) {
        if (!userRepository.existsById(offerDto.getUserId())) {
            throw new ResourceNotFoundException("User", offerDto.getUserId());
        }
        if (!categoryRepository.existsById(offerDto.getCategoryId())) {
            throw new ResourceNotFoundException("Category", offerDto.getCategoryId());
        }
        Offer offer = offerMapper.toEntity(offerDto);
        return offerMapper.toDto(offerRepository.save(offer));
    }

    @Override
    @Transactional
    public OfferDto update(Long id, OfferDto offerDto) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer", id));
        offer.setTitle(offerDto.getTitle());
        offer.setDescription(offerDto.getDescription());
        offer.setStatus(offerDto.getStatus());
        if (offerDto.getCategoryId() != null) {
            categoryRepository.findById(offerDto.getCategoryId())
                    .ifPresent(offer::setCategory);
        }
        return offerMapper.toDto(offerRepository.save(offer));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!offerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Offer", id);
        }
        offerRepository.deleteById(id);
    }
}
