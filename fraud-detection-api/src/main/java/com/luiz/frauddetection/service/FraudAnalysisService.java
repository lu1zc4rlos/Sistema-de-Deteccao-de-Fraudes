package com.luiz.frauddetection.service;

import com.luiz.frauddetection.model.Enum.Device;
import com.luiz.frauddetection.model.Enum.FraudReason;
import com.luiz.frauddetection.model.Enum.Status;
import com.luiz.frauddetection.model.dto.fraudAnalysis.FraudAnalysisResult;
import com.luiz.frauddetection.model.entity.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FraudAnalysisService {

    private final OnnxInferenceService onnxInferenceService;

    public FraudAnalysisService(OnnxInferenceService onnxInferenceService) {
        this.onnxInferenceService = onnxInferenceService;
    }

    public FraudAnalysisResult analyze(Transaction transaction) {
        FraudAnalysisResult result = new FraudAnalysisResult();
        List<FraudReason> reasons = new ArrayList<>();

        // 1. Trava Rígida de Negócio (Hard Rule)
        if (transaction.getAmount().compareTo(new BigDecimal("10000")) > 0) {
            reasons.add(FraudReason.HIGH_AMOUNT);
            result.setReasons(reasons);
            result.setRiskScore(100);
            result.setStatus(Status.BLOCKED);
            return result;
        }

        try {
            // 2. Extração de Features para o modelo ONNX
            float amount = transaction.getAmount().floatValue();
            float isForeign = !"BR".equalsIgnoreCase(transaction.getLocation()) ? 1.0f : 0.0f;
            float isUnknownDevice = (transaction.getDevice() == Device.UNKNOWN || transaction.getDevice() == Device.NEW_DEVICE) ? 1.0f : 0.0f;
            float hourOfDay = (transaction.getTransactionTime() != null)
                    ? (float) transaction.getTransactionTime().getHour()
                    : (float) LocalDateTime.now().getHour();

            float[] inputFeatures = new float[]{ amount, isForeign, isUnknownDevice, hourOfDay };

            // 3. Inferência da Rede Neural (Retorna probabilidade de 0.0 a 1.0)
            float probability = onnxInferenceService.predictFraudProbability(inputFeatures);
            int riskScore = Math.round(probability * 100);

            // 4. Tradução da IA para Regras de Negócio (Explicabilidade)
            if (riskScore > 30) {
                if (isForeign == 1.0f) reasons.add(FraudReason.UNUSUAL_LOCATION);
                if (transaction.getDevice() == Device.NEW_DEVICE) reasons.add(FraudReason.NEW_DEVICE);
                else if (transaction.getDevice() == Device.UNKNOWN) reasons.add(FraudReason.UNKNOWN_DEVICE);
                if (amount > 5000) reasons.add(FraudReason.HIGH_AMOUNT);

                // O trunfo do ML: Score alto sem acionar gatilhos óbvios significa padrão anômalo
                if (reasons.isEmpty() && riskScore > 50) {
                    reasons.add(FraudReason.BEHAVIOR_ANOMALY);
                }
            }

            // 5. Definição do Status Final
            if (riskScore <= 30) result.setStatus(Status.APPROVED);
            else if (riskScore <= 70) result.setStatus(Status.SUSPICIOUS);
            else result.setStatus(Status.BLOCKED);

            result.setRiskScore(riskScore);
            result.setReasons(reasons);
            return result;

        } catch (Exception e) {
            // 6. Resiliência: Em caso de falha do ONNX Runtime, recorre ao método legado original
            return fallbackHeuristicAnalyze(transaction);
        }
    }

    private FraudAnalysisResult fallbackHeuristicAnalyze(Transaction transaction) {
        FraudAnalysisResult result = new FraudAnalysisResult();
        int riskScore = 0;
        List<FraudReason> reasons = new ArrayList<>();

        if (transaction.getAmount().compareTo(new BigDecimal("1000")) <= 0) {
            riskScore += 10;
        } else if (transaction.getAmount().compareTo(new BigDecimal("1000")) > 0 &&
                transaction.getAmount().compareTo(new BigDecimal("5000")) <= 0) {
            riskScore += 35;
        } else if (transaction.getAmount().compareTo(new BigDecimal("5000")) > 0) {
            riskScore += 70;
            reasons.add(FraudReason.HIGH_AMOUNT);
        }

        if (!"BR".equals(transaction.getLocation())) {
            riskScore += 20;
            reasons.add(FraudReason.UNUSUAL_LOCATION);
        }

        if (transaction.getDevice() == Device.UNKNOWN) {
            riskScore += 15;
            reasons.add(FraudReason.UNKNOWN_DEVICE);
        }
        if (transaction.getDevice() == Device.NEW_DEVICE) {
            riskScore += 15;
            reasons.add(FraudReason.NEW_DEVICE);
        }

        if (riskScore > 100) riskScore = 100;
        if (riskScore <= 30) result.setStatus(Status.APPROVED);
        else if (riskScore <= 70) result.setStatus(Status.SUSPICIOUS);
        else result.setStatus(Status.BLOCKED);

        result.setRiskScore(riskScore);
        result.setReasons(reasons);
        return result;
    }
}