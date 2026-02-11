package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.RecommendationDto;
import com.example.bartexchangeai.exception.InvalidOperationException;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.model.exchange.Exchange;
import com.example.bartexchangeai.model.exchange.ExchangeStatus;
import com.example.bartexchangeai.model.offer.Category;
import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.offer.OfferStatus;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.repository.ExchangeRepository;
import com.example.bartexchangeai.repository.OfferRepository;
import com.example.bartexchangeai.repository.ReviewRepository;
import com.example.bartexchangeai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recommendationService, "maxResults", 5);
        ReflectionTestUtils.setField(recommendationService, "maxCandidates", 50);
    }

    @Test
    void getRecommendationsForUser_success() {
        User user = createUser(1L, "alice");
        User otherUser = createUser(2L, "bob");
        Category category = createCategory(1L, "Electronics");
        Offer candidateOffer = createOffer(10L, "Laptop", category, otherUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(offerRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(exchangeRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(reviewRepository.findAverageRatingByReviewedUserId(1L)).thenReturn(4.0);
        when(offerRepository.findByStatus(OfferStatus.ACTIVE)).thenReturn(List.of(candidateOffer));
        when(reviewRepository.findAverageRatingByReviewedUserId(2L)).thenReturn(3.5);

        List<RecommendationDto> expected = List.of(
                new RecommendationDto(10L, "Laptop", "Electronics", 0.9, "Good match")
        );

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.entity(any(ParameterizedTypeReference.class))).thenReturn(expected);

        List<RecommendationDto> result = recommendationService.getRecommendationsForUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getOfferId());
        assertEquals("Laptop", result.get(0).getOfferTitle());
        verify(userRepository).findById(1L);
        verify(chatClient).prompt();
    }

    @Test
    void getRecommendationsForUser_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> recommendationService.getRecommendationsForUser(99L));
        verify(userRepository).findById(99L);
        verify(chatClient, never()).prompt();
    }

    @Test
    void getRecommendationsForUser_noCandidates_returnsEmpty() {
        User user = createUser(1L, "alice");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(offerRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(exchangeRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(reviewRepository.findAverageRatingByReviewedUserId(1L)).thenReturn(null);
        when(offerRepository.findByStatus(OfferStatus.ACTIVE)).thenReturn(Collections.emptyList());

        List<RecommendationDto> result = recommendationService.getRecommendationsForUser(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chatClient, never()).prompt();
    }

    @Test
    void getRecommendationsForUser_excludesOwnOffers() {
        User user = createUser(1L, "alice");
        Category category = createCategory(1L, "Books");
        Offer ownOffer = createOffer(5L, "My Book", category, user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(offerRepository.findByUserId(1L)).thenReturn(List.of(ownOffer));
        when(exchangeRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(reviewRepository.findAverageRatingByReviewedUserId(1L)).thenReturn(null);
        when(offerRepository.findByStatus(OfferStatus.ACTIVE)).thenReturn(List.of(ownOffer));

        List<RecommendationDto> result = recommendationService.getRecommendationsForUser(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chatClient, never()).prompt();
    }

    @Test
    void getRecommendationsForUser_llmError_throwsInvalidOperationException() {
        User user = createUser(1L, "alice");
        User otherUser = createUser(2L, "bob");
        Category category = createCategory(1L, "Art");
        Offer candidateOffer = createOffer(10L, "Painting", category, otherUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(offerRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(exchangeRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(reviewRepository.findAverageRatingByReviewedUserId(1L)).thenReturn(null);
        when(offerRepository.findByStatus(OfferStatus.ACTIVE)).thenReturn(List.of(candidateOffer));
        when(reviewRepository.findAverageRatingByReviewedUserId(2L)).thenReturn(null);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenThrow(new RuntimeException("API timeout"));

        assertThrows(InvalidOperationException.class,
                () -> recommendationService.getRecommendationsForUser(1L));
    }

    private User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        return user;
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    private Offer createOffer(Long id, String title, Category category, User user) {
        Offer offer = new Offer();
        offer.setId(id);
        offer.setTitle(title);
        offer.setDescription("Description for " + title);
        offer.setStatus(OfferStatus.ACTIVE);
        offer.setCategory(category);
        offer.setUser(user);
        return offer;
    }
}
