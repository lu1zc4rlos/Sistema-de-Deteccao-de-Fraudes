package com.luiz.frauddetection.repository;

import com.luiz.frauddetection.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    <S extends Transaction> S save(S entity);
}
