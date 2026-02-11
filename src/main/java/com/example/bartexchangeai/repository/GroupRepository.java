package com.example.bartexchangeai.repository;

import com.example.bartexchangeai.model.group.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    Optional<Group> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.id = :userId")
    List<Group> findByMemberId(Long userId);
    
    @Query("SELECT g FROM Group g WHERE g.name LIKE %:keyword% OR g.description LIKE %:keyword%")
    List<Group> findByNameOrDescriptionContaining(String keyword);

    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.id = :userId")
    Page<Group> findPageByMemberId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT g FROM Group g WHERE g.name LIKE %:keyword% OR g.description LIKE %:keyword%")
    Page<Group> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}