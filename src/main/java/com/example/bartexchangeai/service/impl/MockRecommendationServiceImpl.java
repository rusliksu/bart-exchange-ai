package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.RecommendationDto;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.offer.OfferStatus;
import com.example.bartexchangeai.repository.OfferRepository;
import com.example.bartexchangeai.repository.UserRepository;
import com.example.bartexchangeai.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@ConditionalOnProperty(name = "app.recommendation.mock-enabled", havingValue = "true")
public class MockRecommendationServiceImpl implements RecommendationService {

    private final UserRepository userRepository;
    private final OfferRepository offerRepository;

    @Override
    public List<RecommendationDto> getRecommendationsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }

        return offerRepository.findByStatus(OfferStatus.ACTIVE).stream()
                .filter(o -> !o.getUser().getId().equals(userId))
                .limit(5)
                .map(this::toMockRecommendation)
                .toList();
    }

    private RecommendationDto toMockRecommendation(Offer offer) {
        return new RecommendationDto(
                offer.getId(),
                offer.getTitle(),
                offer.getCategory().getName(),
                0.5,
                "Mock recommendation (AI disabled in dev profile)"
        );
    }
}
