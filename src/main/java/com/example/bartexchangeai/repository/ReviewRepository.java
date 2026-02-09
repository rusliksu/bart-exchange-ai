package com.example.bartexchangeai.repository;

import com.example.bartexchangeai.model.user.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByReviewerId(Long reviewerId);
    
    List<Review> findByReviewedUserId(Long reviewedUserId);
    
    List<Review> findByExchangeId(Long exchangeId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUser.id = :userId")
    Double findAverageRatingByReviewedUserId(Long userId);
    
    @Query("SELECT r FROM Review r WHERE r.reviewedUser.id = :userId ORDER BY r.id DESC")
    List<Review> findByReviewedUserIdOrderByIdDesc(Long userId);
    
    boolean existsByExchangeIdAndReviewerId(Long exchangeId, Long reviewerId);
}