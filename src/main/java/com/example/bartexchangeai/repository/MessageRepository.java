package com.example.bartexchangeai.repository;

import com.example.bartexchangeai.model.exchange.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByExchangeId(Long exchangeId);
    
    List<Message> findBySenderId(Long senderId);
    
    @Query("SELECT m FROM Message m WHERE m.exchange.id = :exchangeId ORDER BY m.timestamp ASC")
    List<Message> findByExchangeIdOrderByTimestampAsc(Long exchangeId);
    
    @Query("SELECT m FROM Message m WHERE m.exchange.id = :exchangeId ORDER BY m.timestamp DESC")
    List<Message> findByExchangeIdOrderByTimestampDesc(Long exchangeId);

    @Query("SELECT COUNT(m) > 0 FROM Message m WHERE m.id = :messageId AND m.sender.username = :username")
    boolean existsByIdAndSenderUsername(@Param("messageId") Long messageId, @Param("username") String username);
}