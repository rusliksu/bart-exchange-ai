package com.example.bartexchangeai.security;

import com.example.bartexchangeai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final ExchangeRepository exchangeRepository;
    private final MessageRepository messageRepository;
    private final ReviewRepository reviewRepository;

    public boolean isUserSelf(Long userId, String username) {
        return userRepository.existsByIdAndUsername(userId, username);
    }

    public boolean isOfferOwner(Long offerId, String username) {
        return offerRepository.existsByIdAndUserUsername(offerId, username);
    }

    public boolean isExchangeParticipant(Long exchangeId, String username) {
        return exchangeRepository.existsByIdAndParticipantUsername(exchangeId, username);
    }

    public boolean isMessageSender(Long messageId, String username) {
        return messageRepository.existsByIdAndSenderUsername(messageId, username);
    }

    public boolean isReviewAuthor(Long reviewId, String username) {
        return reviewRepository.existsByIdAndReviewerUsername(reviewId, username);
    }
}
