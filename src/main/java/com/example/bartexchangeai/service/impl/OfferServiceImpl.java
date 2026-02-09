package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.OfferDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.OfferMapper;
import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.repository.CategoryRepository;
import com.example.bartexchangeai.repository.OfferRepository;
import com.example.bartexchangeai.repository.UserRepository;
import com.example.bartexchangeai.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<OfferDto> findByStatus(String status) {
        return offerMapper.toDtoList(offerRepository.findByStatus(status));
    }

    @Override
    public List<OfferDto> findByUserId(Long userId) {
        return offerMapper.toDtoList(offerRepository.findByUserId(userId));
    }

    @Override
    public List<OfferDto> findByCategoryId(Long categoryId) {
        return offerMapper.toDtoList(offerRepository.findByCategoryId(categoryId));
    }

    @Override
    public List<OfferDto> search(String keyword) {
        return offerMapper.toDtoList(offerRepository.findByTitleOrDescriptionContaining(keyword));
    }

    @Override
    public List<OfferDto> findActive() {
        return offerMapper.toDtoList(offerRepository.findActiveOffersOrderByNewest());
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
