package com.example.bartexchangeai.repository;

import com.example.bartexchangeai.model.user.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;

import java.util.Collection;
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

    @Query("SELECT r.reviewedUser.id, AVG(r.rating) FROM Review r WHERE r.reviewedUser.id IN :userIds GROUP BY r.reviewedUser.id")
    List<Object[]> findAverageRatingsByUserIds(@Param("userIds") Collection<Long> userIds);

    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.id = :reviewId AND r.reviewer.username = :username")
    boolean existsByIdAndReviewerUsername(@Param("reviewId") Long reviewId, @Param("username") String username);

    Page<Review> findByReviewerId(Long reviewerId, Pageable pageable);

    Page<Review> findByReviewedUserId(Long reviewedUserId, Pageable pageable);

    Page<Review> findByExchangeId(Long exchangeId, Pageable pageable);
}