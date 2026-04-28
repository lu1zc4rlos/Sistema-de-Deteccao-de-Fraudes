package com.luiz.frauddetection.mapper;

import com.luiz.frauddetection.model.dto.transaction.TransactionRequest;
import com.luiz.frauddetection.model.dto.transaction.TransactionResponse;
import com.luiz.frauddetection.model.entity.FraudLog;
import com.luiz.frauddetection.model.entity.Transaction;
import com.luiz.frauddetection.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final FraudLogMapper fraudLogMapper;

    public Transaction toEntity(TransactionRequest request, User user){

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(request.getAmount());
        transaction.setLocation(request.getLocation());
        transaction.setDevice(request.getDevice());

        return transaction;
    }

    public TransactionResponse toResponse(Transaction transaction, List<FraudLog> fraudLogs){

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setId(transaction.getId());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setLocation(transaction.getLocation());
        transactionResponse.setDevice(transaction.getDevice());
        transactionResponse.setTimestamp(transaction.getTransactionTime());
        transactionResponse.setRiskScore(transaction.getRiskScore());
        transactionResponse.setStatus(transaction.getStatus());
        transactionResponse.setFraudLogs(
                fraudLogs.stream()
                        .map(fraudLogMapper::toResponse)
                        .toList()
        );

        return transactionResponse;
    }
}
