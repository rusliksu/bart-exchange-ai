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
        return userRepository.findById(userId)
                .map(u -> u.getUsername().equals(username))
                .orElse(false);
    }

    public boolean isOfferOwner(Long offerId, String username) {
        return offerRepository.findById(offerId)
                .map(o -> o.getUser().getUsername().equals(username))
                .orElse(false);
    }

    public boolean isExchangeParticipant(Long exchangeId, String username) {
        return exchangeRepository.findById(exchangeId)
                .map(e -> e.getInitiator().getUsername().equals(username)
                        || e.getParticipant().getUsername().equals(username))
                .orElse(false);
    }

    public boolean isMessageSender(Long messageId, String username) {
        return messageRepository.findById(messageId)
                .map(m -> m.getSender().getUsername().equals(username))
                .orElse(false);
    }

    public boolean isReviewAuthor(Long reviewId, String username) {
        return reviewRepository.findById(reviewId)
                .map(r -> r.getReviewer().getUsername().equals(username))
                .orElse(false);
    }
}
