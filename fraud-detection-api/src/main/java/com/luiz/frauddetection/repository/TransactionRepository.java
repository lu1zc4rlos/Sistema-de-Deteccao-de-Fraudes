package com.luiz.frauddetection.repository;

import com.luiz.frauddetection.model.dto.transaction.TransactionStatsResponse;
import com.luiz.frauddetection.model.entity.Transaction;
import com.luiz.frauddetection.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    <S extends Transaction> S save(S entity);
    List<Transaction> findByUser(User user);

    @Query("""
    SELECT new com.luiz.frauddetection.model.dto.transaction.TransactionStatsResponse(
        COUNT(t),
        COALESCE(SUM(t.amount), 0),
        COALESCE(AVG(t.riskScore), 0.0),
        COUNT(CASE WHEN t.status = 'APPROVED' THEN 1 END),
        COUNT(CASE WHEN t.status = 'SUSPICIOUS' THEN 1 END),
        COUNT(CASE WHEN t.status = 'BLOCKED' THEN 1 END)
    )
    FROM Transaction t
    WHERE t.user = :user 
"""
    )
    TransactionStatsResponse getStatsByUser(@Param("user") User user);
}
