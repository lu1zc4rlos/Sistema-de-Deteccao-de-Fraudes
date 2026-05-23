package com.luiz.frauddetection.repository;

import com.luiz.frauddetection.model.entity.FraudLog;
import com.luiz.frauddetection.model.entity.Transaction;
import com.luiz.frauddetection.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraudLogRepository extends JpaRepository<FraudLog, Long> {
    <S extends FraudLog> S save(S entity);
    List<FraudLog> findByTransactionId(Long transactionId);
}
