package com.example.bartexchangeai.repository;

import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.offer.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;

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

    @Query("SELECT COUNT(o) > 0 FROM Offer o WHERE o.id = :offerId AND o.user.username = :username")
    boolean existsByIdAndUserUsername(@Param("offerId") Long offerId, @Param("username") String username);

    Page<Offer> findByStatus(OfferStatus status, Pageable pageable);

    Page<Offer> findByUserId(Long userId, Pageable pageable);

    Page<Offer> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT o FROM Offer o WHERE o.title LIKE %:keyword% OR o.description LIKE %:keyword%")
    Page<Offer> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT o FROM Offer o WHERE o.status = 'ACTIVE'")
    Page<Offer> findActiveOffers(Pageable pageable);
}
