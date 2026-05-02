package com.luiz.frauddetection.service;

import com.luiz.frauddetection.mapper.TransactionMapper;
import com.luiz.frauddetection.model.Enum.FraudReason;
import com.luiz.frauddetection.model.dto.fraudAnalysis.FraudAnalysisResult;
import com.luiz.frauddetection.model.dto.transaction.TransactionRequest;
import com.luiz.frauddetection.model.dto.transaction.TransactionResponse;
import com.luiz.frauddetection.model.entity.FraudLog;
import com.luiz.frauddetection.model.entity.Transaction;
import com.luiz.frauddetection.model.entity.User;
import com.luiz.frauddetection.repository.FraudLogRepository;
import com.luiz.frauddetection.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final FraudLogRepository fraudLogRepository;
    private final TransactionRepository transactionRepository;
    private final FraudAnalysisService fraudAnalysisService;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, User user){

        Transaction transaction = transactionMapper.toEntity(request, user);
        FraudAnalysisResult fraudAnalysisResult = fraudAnalysisService.analyze(transaction);

        transaction.setRiskScore(fraudAnalysisResult.getRiskScore());
        transaction.setStatus(fraudAnalysisResult.getStatus());
        transactionRepository.save(transaction);

        List<FraudLog> fraudLogs = saveFraudLogs(transaction,fraudAnalysisResult);
        TransactionResponse transactionResponse = transactionMapper.toResponse(transaction,fraudLogs);

        return transactionResponse;
    }

    @Transactional
    private List<FraudLog> saveFraudLogs(Transaction transaction, FraudAnalysisResult fraudAnalysisResult){

        List<FraudLog> fraudLogs = new ArrayList<>();

        for(FraudReason reason : fraudAnalysisResult.getReasons()){

            FraudLog fraudLog = new FraudLog();
            fraudLog.setTransaction(transaction);
            fraudLog.setReason(reason);

            FraudLog fraudLogSave = fraudLogRepository.save(fraudLog);
            fraudLogs.add(fraudLogSave);
        }

        return fraudLogs;
    }
}
