package com.luiz.frauddetection.service;

import com.luiz.frauddetection.mapper.TransactionMapper;
import com.luiz.frauddetection.model.dto.transaction.TransactionRequest;
import com.luiz.frauddetection.model.dto.transaction.TransactionResponse;
import com.luiz.frauddetection.model.entity.Transaction;
import com.luiz.frauddetection.model.entity.User;
import com.luiz.frauddetection.repository.FraudLogRepository;
import com.luiz.frauddetection.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final FraudLogRepository fraudLogRepository;
    private final TransactionRepository transactionRepository;
    private final FraudAnalysisService fraudAnalysisService;
    private final TransactionMapper transactionMapper;


}
