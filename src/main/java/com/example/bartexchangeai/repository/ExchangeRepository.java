package com.example.bartexchangeai.repository;

import com.example.bartexchangeai.model.exchange.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    
    List<Exchange> findByStatus(String status);
    
    List<Exchange> findByInitiatorId(Long initiatorId);
    
    List<Exchange> findByParticipantId(Long participantId);
    
    List<Exchange> findByOfferId(Long offerId);
    
    @Query("SELECT e FROM Exchange e WHERE e.initiator.id = :userId OR e.participant.id = :userId")
    List<Exchange> findByUserId(Long userId);
    
    @Query("SELECT e FROM Exchange e WHERE e.status = :status AND (e.initiator.id = :userId OR e.participant.id = :userId)")
    List<Exchange> findByStatusAndUserId(String status, Long userId);
    
    @Query("SELECT e FROM Exchange e ORDER BY e.date DESC")
    List<Exchange> findAllOrderByDateDesc();
}