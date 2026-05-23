package com.luiz.frauddetection.repository;

import com.luiz.frauddetection.model.entity.Transaction;
import com.luiz.frauddetection.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    <S extends Transaction> S save(S entity);
    java.util.List<Transaction> findByUserOrderByTransactionTimeDesc(com.luiz.frauddetection.model.entity.User user);
    java.util.List<Transaction> findByUserIdOrderByTransactionTimeDesc(Long userId);
    List<Transaction> findByUser(User user);
}
