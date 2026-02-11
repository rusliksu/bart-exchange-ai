package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.RecommendationDto;
import com.example.bartexchangeai.exception.InvalidOperationException;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.model.exchange.Exchange;
import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.offer.OfferStatus;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.repository.ExchangeRepository;
import com.example.bartexchangeai.repository.OfferRepository;
import com.example.bartexchangeai.repository.ReviewRepository;
import com.example.bartexchangeai.repository.UserRepository;
import com.example.bartexchangeai.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@ConditionalOnProperty(name = "app.recommendation.mock-enabled", havingValue = "false", matchIfMissing = true)
public class RecommendationServiceImpl implements RecommendationService {

    private final ChatClient chatClient;
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final ExchangeRepository exchangeRepository;
    private final ReviewRepository reviewRepository;

    @Value("${app.recommendation.max-results:5}")
    private int maxResults;

    @Value("${app.recommendation.max-candidates:50}")
    private int maxCandidates;

    @Override
    public List<RecommendationDto> getRecommendationsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        List<Offer> userOffers = offerRepository.findByUserId(userId);
        List<Exchange> userExchanges = exchangeRepository.findByUserId(userId);
        Double avgRating = reviewRepository.findAverageRatingByReviewedUserId(userId);

        Set<Long> userOfferIds = userOffers.stream()
                .map(Offer::getId)
                .collect(Collectors.toSet());

        List<Offer> candidates = offerRepository.findByStatus(OfferStatus.ACTIVE).stream()
                .filter(o -> !o.getUser().getId().equals(userId))
                .limit(maxCandidates)
                .toList();

        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(user, userOffers, userExchanges, avgRating, candidates);

        try {
            List<RecommendationDto> recommendations = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .entity(new ParameterizedTypeReference<>() {});

            if (recommendations == null) {
                return Collections.emptyList();
            }

            return recommendations.stream()
                    .limit(maxResults)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get recommendations from LLM for user {}: {}", userId, e.getMessage());
            throw new InvalidOperationException("Не удалось получить рекомендации: " + e.getMessage());
        }
    }

    private String buildSystemPrompt() {
        return """
                You are a recommendation engine for a barter exchange platform. \
                Analyze the user's profile and exchange history, then rank the candidate offers \
                by relevance. Return a JSON array where each element has: \
                offerId (Long), offerTitle (String), categoryName (String), \
                score (Double, 0.0 to 1.0), reason (String, brief explanation). \
                No markdown, no extra text — only valid JSON array.""";
    }

    private String buildUserPrompt(User user, List<Offer> userOffers,
                                   List<Exchange> exchanges, Double avgRating,
                                   List<Offer> candidates) {
        StringBuilder sb = new StringBuilder();

        sb.append("## User Profile\n");
        sb.append("Username: ").append(user.getUsername()).append("\n");
        sb.append("Rating: ").append(avgRating != null ? String.format("%.1f", avgRating) : "no ratings yet").append("\n");

        sb.append("\n## User's Offers\n");
        if (userOffers.isEmpty()) {
            sb.append("None\n");
        } else {
            for (Offer o : userOffers) {
                sb.append("- ").append(o.getTitle())
                        .append(" [").append(o.getCategory().getName()).append("]")
                        .append(" (").append(o.getStatus()).append(")\n");
            }
        }

        sb.append("\n## Exchange History (").append(exchanges.size()).append(" exchanges)\n");
        if (!exchanges.isEmpty()) {
            Set<String> exchangedCategories = exchanges.stream()
                    .map(e -> e.getOffer().getCategory().getName())
                    .collect(Collectors.toSet());
            sb.append("Categories involved: ").append(String.join(", ", exchangedCategories)).append("\n");
        }

        sb.append("\n## Candidate Offers to Rank\n");
        for (Offer c : candidates) {
            Double ownerRating = reviewRepository.findAverageRatingByReviewedUserId(c.getUser().getId());
            sb.append("- ID: ").append(c.getId())
                    .append(", Title: ").append(c.getTitle())
                    .append(", Category: ").append(c.getCategory().getName())
                    .append(", Description: ").append(c.getDescription() != null ? c.getDescription() : "N/A")
                    .append(", Owner rating: ").append(ownerRating != null ? String.format("%.1f", ownerRating) : "N/A")
                    .append("\n");
        }

        sb.append("\nRank the top ").append(maxResults).append(" most relevant candidate offers for this user.");

        return sb.toString();
    }
}
