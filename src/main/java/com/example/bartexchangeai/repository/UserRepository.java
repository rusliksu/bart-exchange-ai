package com.example.bartexchangeai.repository;

import com.example.bartexchangeai.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.rating >= :minRating ORDER BY u.rating DESC")
    List<User> findByRatingGreaterThanEqualOrderByRatingDesc(Float minRating);
    
    @Query("SELECT u FROM User u JOIN u.groups g WHERE g.id = :groupId")
    List<User> findByGroupId(Long groupId);

    boolean existsByIdAndUsername(Long id, String username);

    @Query("SELECT u FROM User u WHERE u.rating >= :minRating")
    Page<User> findByMinRating(@Param("minRating") Float minRating, Pageable pageable);
}