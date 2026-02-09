package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.repository.OfferRepository;
import com.example.bartexchangeai.repository.UserRepository;
import com.example.bartexchangeai.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {
    
    private final OfferRepository offerRepository;
    
    private final UserRepository userRepository;
    
    private final CategoryRepository categoryRepository;
    
    @GetMapping
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        Optional<Offer> offer = offerRepository.findById(id);
        return offer.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public List<Offer> getOffersByStatus(@PathVariable String status) {
        return offerRepository.findByStatus(status);
    }
    
    @GetMapping("/user/{userId}")
    public List<Offer> getOffersByUser(@PathVariable Long userId) {
        return offerRepository.findByUserId(userId);
    }
    
    @GetMapping("/category/{categoryId}")
    public List<Offer> getOffersByCategory(@PathVariable Long categoryId) {
        return offerRepository.findByCategoryId(categoryId);
    }
    
    @GetMapping("/search")
    public List<Offer> searchOffers(@RequestParam String keyword) {
        return offerRepository.findByTitleOrDescriptionContaining(keyword);
    }
    
    @GetMapping("/active")
    public List<Offer> getActiveOffers() {
        return offerRepository.findActiveOffersOrderByNewest();
    }
    
    @PostMapping
    public ResponseEntity<Offer> createOffer(@RequestBody Offer offer) {
        // Validate user and category exist
        if (!userRepository.existsById(offer.getUser().getId()) || 
            !categoryRepository.existsById(offer.getCategory().getId())) {
            return ResponseEntity.badRequest().build();
        }
        Offer savedOffer = offerRepository.save(offer);
        return ResponseEntity.ok(savedOffer);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable Long id, @RequestBody Offer offerDetails) {
        Optional<Offer> optionalOffer = offerRepository.findById(id);
        if (optionalOffer.isPresent()) {
            Offer offer = optionalOffer.get();
            offer.setTitle(offerDetails.getTitle());
            offer.setDescription(offerDetails.getDescription());
            offer.setStatus(offerDetails.getStatus());
            if (offerDetails.getCategory() != null) {
                offer.setCategory(offerDetails.getCategory());
            }
            Offer updatedOffer = offerRepository.save(offer);
            return ResponseEntity.ok(updatedOffer);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        if (offerRepository.existsById(id)) {
            offerRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}