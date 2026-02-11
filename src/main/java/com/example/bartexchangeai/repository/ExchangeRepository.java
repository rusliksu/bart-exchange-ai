package com.example.bartexchangeai.repository;

import com.example.bartexchangeai.model.exchange.Exchange;
import com.example.bartexchangeai.model.exchange.ExchangeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    List<Exchange> findByStatus(ExchangeStatus status);

    List<Exchange> findByInitiatorId(Long initiatorId);

    List<Exchange> findByParticipantId(Long participantId);

    List<Exchange> findByOfferId(Long offerId);

    @Query("SELECT e FROM Exchange e WHERE e.initiator.id = :userId OR e.participant.id = :userId")
    List<Exchange> findByUserId(Long userId);

    @Query("SELECT e FROM Exchange e WHERE e.status = :status AND (e.initiator.id = :userId OR e.participant.id = :userId)")
    List<Exchange> findByStatusAndUserId(ExchangeStatus status, Long userId);

    @Query("SELECT e FROM Exchange e ORDER BY e.date DESC")
    List<Exchange> findAllOrderByDateDesc();

    @Query("SELECT COUNT(e) > 0 FROM Exchange e WHERE e.id = :exchangeId AND (e.initiator.username = :username OR e.participant.username = :username)")
    boolean existsByIdAndParticipantUsername(@Param("exchangeId") Long exchangeId, @Param("username") String username);

    Page<Exchange> findByStatus(ExchangeStatus status, Pageable pageable);

    Page<Exchange> findByInitiatorId(Long initiatorId, Pageable pageable);

    Page<Exchange> findByParticipantId(Long participantId, Pageable pageable);

    @Query("SELECT e FROM Exchange e WHERE e.initiator.id = :userId OR e.participant.id = :userId")
    Page<Exchange> findPageByUserId(@Param("userId") Long userId, Pageable pageable);
}
