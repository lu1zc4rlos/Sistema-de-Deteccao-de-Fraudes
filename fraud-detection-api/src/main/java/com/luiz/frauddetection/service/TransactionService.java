package com.luiz.frauddetection.service;

import com.luiz.frauddetection.mapper.TransactionMapper;
import com.luiz.frauddetection.model.Enum.FraudReason;
import com.luiz.frauddetection.model.dto.fraudAnalysis.FraudAnalysisResult;
import com.luiz.frauddetection.model.dto.transaction.TransactionRequest;
import com.luiz.frauddetection.model.dto.transaction.TransactionResponse;
import com.luiz.frauddetection.model.dto.transaction.TransactionStatsResponse;
import com.luiz.frauddetection.model.Enum.Status;
import com.luiz.frauddetection.model.entity.FraudLog;
import com.luiz.frauddetection.model.entity.Transaction;
import com.luiz.frauddetection.model.entity.User;
import com.luiz.frauddetection.repository.FraudLogRepository;
import com.luiz.frauddetection.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final FraudLogRepository fraudLogRepository;
    private final TransactionRepository transactionRepository;
    private final FraudAnalysisService fraudAnalysisService;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, User user) {
        try {
            Transaction transaction = transactionMapper.toEntity(request, user);
            FraudAnalysisResult fraudAnalysisResult = fraudAnalysisService.analyze(transaction);
            
            transaction.setRiskScore(fraudAnalysisResult.getRiskScore());
            transaction.setStatus(fraudAnalysisResult.getStatus());
            transaction = transactionRepository.save(transaction);

            List<FraudLog> fraudLogs = saveFraudLogs(transaction, fraudAnalysisResult);
            return transactionMapper.toResponse(transaction, fraudLogs);

        } catch (Exception e) {
            System.err.println("Erro ao criar transação: " + e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getUserHistory(User user) {
        // Usar ID para garantir consistência na consulta
        return transactionRepository.findByUserIdOrderByTransactionTimeDesc(user.getId())
                .stream()
                .map(t -> {
                    List<FraudLog> logs = fraudLogRepository.findByTransaction(t);
                    return transactionMapper.toResponse(t, logs);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionStatsResponse getUserStats(User user) {
        // Usar ID para garantir consistência na consulta
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionTimeDesc(user.getId());
        
        if (transactions.isEmpty()) {
            return TransactionStatsResponse.builder()
                    .totalTransactions(0)
                    .totalAmount(BigDecimal.ZERO)
                    .averageRiskScore(0.0)
                    .approvedCount(0)
                    .suspiciousCount(0)
                    .blockedCount(0)
                    .build();
        }

        long totalTransactions = transactions.size();
        BigDecimal totalAmount = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        double avgRisk = transactions.stream()
                .mapToInt(Transaction::getRiskScore)
                .average()
                .orElse(0.0);

        long approved = transactions.stream().filter(t -> t.getStatus() == Status.APPROVED).count();
        long suspicious = transactions.stream().filter(t -> t.getStatus() == Status.SUSPICIOUS).count();
        long blocked = transactions.stream().filter(t -> t.getStatus() == Status.BLOCKED).count();

        return TransactionStatsResponse.builder()
                .totalTransactions(totalTransactions)
                .totalAmount(totalAmount)
                .averageRiskScore(avgRisk)
                .approvedCount(approved)
                .suspiciousCount(suspicious)
                .blockedCount(blocked)
                .build();
    }

    @Transactional
    private List<FraudLog> saveFraudLogs(Transaction transaction, FraudAnalysisResult fraudAnalysisResult){
        List<FraudLog> fraudLogs = new ArrayList<>();
        for(FraudReason reason : fraudAnalysisResult.getReasons()){
            FraudLog fraudLog = new FraudLog();
            fraudLog.setTransaction(transaction);
            fraudLog.setReason(reason);
            fraudLogs.add(fraudLogRepository.save(fraudLog));
        }
        return fraudLogs;
    }
}
