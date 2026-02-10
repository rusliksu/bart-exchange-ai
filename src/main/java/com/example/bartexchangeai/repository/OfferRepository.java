package com.example.bartexchangeai.repository;

import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.offer.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByStatus(OfferStatus status);

    List<Offer> findByUserId(Long userId);

    List<Offer> findByCategoryId(Long categoryId);

    List<Offer> findByGroupId(Long groupId);

    @Query("SELECT o FROM Offer o WHERE o.status = :status AND o.category.id = :categoryId")
    List<Offer> findByStatusAndCategoryId(OfferStatus status, Long categoryId);

    @Query("SELECT o FROM Offer o WHERE o.title LIKE %:keyword% OR o.description LIKE %:keyword%")
    List<Offer> findByTitleOrDescriptionContaining(String keyword);

    @Query("SELECT o FROM Offer o WHERE o.status = 'ACTIVE' ORDER BY o.id DESC")
    List<Offer> findActiveOffersOrderByNewest();
}
