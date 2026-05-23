package com.luiz.frauddetection.service;

import com.luiz.frauddetection.config.exception.ForbiddenException;
import com.luiz.frauddetection.config.exception.ResourceNotFoundException;
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
import org.springframework.dao.DataIntegrityViolationException;
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
    public TransactionResponse createTransaction(TransactionRequest request, User user) {
        try {
            Transaction transaction = transactionMapper.toEntity(request, user);

            FraudAnalysisResult fraudAnalysisResult = fraudAnalysisService.analyze(transaction);

            transaction.setRiskScore(fraudAnalysisResult.getRiskScore());
            transaction.setStatus(fraudAnalysisResult.getStatus());

            transaction = transactionRepository.save(transaction);

            List<FraudLog> fraudLogs = saveFraudLogs(transaction, fraudAnalysisResult);

            return transactionMapper.toResponse(transaction, fraudLogs);

        } catch (DataIntegrityViolationException e) {
            System.err.println("Erro de integridade de dados ao salvar a transação:");
            e.printStackTrace();
            throw e;

        } catch (RuntimeException e) {
            System.err.println("Erro de lógica ou integração no serviço de análise de fraude:");
            e.printStackTrace();
            throw e;

        } catch (Exception e) {
            System.err.println("Erro inesperado no fluxo de criação de transação:");
            e.printStackTrace();
            throw e;
        }
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

    public List<TransactionResponse> getUserTransactions(User user){

        List<Transaction> transactions = transactionRepository.findByUser(user);

        return transactions.stream()
                .map(transaction -> {
                    List<FraudLog> fraudLogs = fraudLogRepository.findByTransactionId(transaction.getId());
                    return transactionMapper.toResponse(transaction, fraudLogs);
                })
                .toList();
    }

    public TransactionResponse getUserTransaction(User user, Long transactionId){

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket não encontrado."));

        List<FraudLog> fraudLogs = fraudLogRepository.findByTransactionId(transaction.getId());

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Acesso negado. Essa transaction não pertence a este cliente.");
        }

        return transactionMapper.toResponse(transaction, fraudLogs);
    }
}
