package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.model.offer.Category;
import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.offer.OfferStatus;
import com.example.bartexchangeai.model.user.Role;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.repository.CategoryRepository;
import com.example.bartexchangeai.repository.OfferRepository;
import com.example.bartexchangeai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class RecommendationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Test
    void getRecommendations_returnsActiveOffersFromOtherUsers() throws Exception {
        User alice = createUser("alice", "alice@test.com");
        User bob = createUser("bob", "bob@test.com");
        Category electronics = createCategory("Electronics");
        createOffer("Bob's Laptop", electronics, bob, OfferStatus.ACTIVE);
        createOffer("Alice's Phone", electronics, alice, OfferStatus.ACTIVE);

        mockMvc.perform(get("/api/recommendations/user/" + alice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].offerTitle").value("Bob's Laptop"))
                .andExpect(jsonPath("$[0].score").value(0.5));
    }

    @Test
    void getRecommendations_userNotFound_returns404() throws Exception {
        mockMvc.perform(get("/api/recommendations/user/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecommendations_noCandidates_returnsEmptyArray() throws Exception {
        User alice = createUser("alice", "alice@test.com");

        mockMvc.perform(get("/api/recommendations/user/" + alice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRecommendations_excludesInactiveOffers() throws Exception {
        User alice = createUser("alice", "alice@test.com");
        User bob = createUser("bob", "bob@test.com");
        Category books = createCategory("Books");
        createOffer("Active Book", books, bob, OfferStatus.ACTIVE);
        createOffer("Completed Book", books, bob, OfferStatus.COMPLETED);

        mockMvc.perform(get("/api/recommendations/user/" + alice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].offerTitle").value("Active Book"));
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setRating(0.0f);
        return userRepository.save(user);
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    private Offer createOffer(String title, Category category, User user, OfferStatus status) {
        Offer offer = new Offer();
        offer.setTitle(title);
        offer.setDescription("Description for " + title);
        offer.setStatus(status);
        offer.setCategory(category);
        offer.setUser(user);
        return offerRepository.save(offer);
    }
}
