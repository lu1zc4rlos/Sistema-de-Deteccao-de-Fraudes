package com.luiz.frauddetection.repository;

import com.luiz.frauddetection.model.entity.FraudLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudLogRepository extends JpaRepository<FraudLog, Long> {
    <S extends FraudLog> S save(S entity);
}
