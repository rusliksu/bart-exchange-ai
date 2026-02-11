package com.example.bartexchangeai.security;

import com.example.bartexchangeai.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    void isUserSelf_authorized() {
        when(userRepository.existsByIdAndUsername(1L, "alice")).thenReturn(true);

        assertTrue(authorizationService.isUserSelf(1L, "alice"));
        verify(userRepository).existsByIdAndUsername(1L, "alice");
    }

    @Test
    void isUserSelf_unauthorized() {
        when(userRepository.existsByIdAndUsername(1L, "bob")).thenReturn(false);

        assertFalse(authorizationService.isUserSelf(1L, "bob"));
    }

    @Test
    void isOfferOwner_authorized() {
        when(offerRepository.existsByIdAndUserUsername(10L, "alice")).thenReturn(true);

        assertTrue(authorizationService.isOfferOwner(10L, "alice"));
        verify(offerRepository).existsByIdAndUserUsername(10L, "alice");
    }

    @Test
    void isOfferOwner_unauthorized() {
        when(offerRepository.existsByIdAndUserUsername(10L, "bob")).thenReturn(false);

        assertFalse(authorizationService.isOfferOwner(10L, "bob"));
    }

    @Test
    void isExchangeParticipant_authorized() {
        when(exchangeRepository.existsByIdAndParticipantUsername(5L, "alice")).thenReturn(true);

        assertTrue(authorizationService.isExchangeParticipant(5L, "alice"));
        verify(exchangeRepository).existsByIdAndParticipantUsername(5L, "alice");
    }

    @Test
    void isExchangeParticipant_unauthorized() {
        when(exchangeRepository.existsByIdAndParticipantUsername(5L, "charlie")).thenReturn(false);

        assertFalse(authorizationService.isExchangeParticipant(5L, "charlie"));
    }

    @Test
    void isMessageSender_authorized() {
        when(messageRepository.existsByIdAndSenderUsername(3L, "alice")).thenReturn(true);

        assertTrue(authorizationService.isMessageSender(3L, "alice"));
        verify(messageRepository).existsByIdAndSenderUsername(3L, "alice");
    }

    @Test
    void isMessageSender_unauthorized() {
        when(messageRepository.existsByIdAndSenderUsername(3L, "bob")).thenReturn(false);

        assertFalse(authorizationService.isMessageSender(3L, "bob"));
    }

    @Test
    void isReviewAuthor_authorized() {
        when(reviewRepository.existsByIdAndReviewerUsername(7L, "alice")).thenReturn(true);

        assertTrue(authorizationService.isReviewAuthor(7L, "alice"));
        verify(reviewRepository).existsByIdAndReviewerUsername(7L, "alice");
    }

    @Test
    void isReviewAuthor_unauthorized() {
        when(reviewRepository.existsByIdAndReviewerUsername(7L, "bob")).thenReturn(false);

        assertFalse(authorizationService.isReviewAuthor(7L, "bob"));
    }
}
