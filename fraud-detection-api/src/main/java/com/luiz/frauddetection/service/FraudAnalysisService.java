package com.luiz.frauddetection.service;

import com.luiz.frauddetection.model.Enum.Device;
import com.luiz.frauddetection.model.Enum.FraudReason;
import com.luiz.frauddetection.model.Enum.Status;
import com.luiz.frauddetection.model.dto.fraudAnalysis.FraudAnalysisResult;
import com.luiz.frauddetection.model.entity.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class FraudAnalysisService {

    public FraudAnalysisResult analyze(Transaction transaction){

        FraudAnalysisResult fraudAnalysisResult = new FraudAnalysisResult();
        Integer riskScore = 0;
        List<FraudReason> reasons = new ArrayList<>();

        //--------------------------------------------------------//
        if (transaction.getAmount().compareTo(new BigDecimal("1000")) <= 0){
            riskScore += 10;
        }
        else if(transaction.getAmount().compareTo(new BigDecimal("1000")) > 0 &&
                transaction.getAmount().compareTo(new BigDecimal("5000")) <= 0){
            riskScore += 35;
        }
        else if(transaction.getAmount().compareTo(new BigDecimal("5000")) > 0){
            riskScore += 70;
            reasons.add(FraudReason.HIGH_AMOUNT);
        }
        //--------------------------------------------------------//
        if(!"BR".equals(transaction.getLocation())){
            riskScore += 20;
            reasons.add(FraudReason.UNUSUAL_LOCATION);
        }
        //--------------------------------------------------------//
        if(transaction.getDevice() == Device.UNKNOWN){
            riskScore += 15;
            reasons.add(FraudReason.UNKNOWN_DEVICE);
        }
        if(transaction.getDevice() == Device.NEW_DEVICE){
            riskScore += 15;
            reasons.add(FraudReason.NEW_DEVICE);
        }
        //--------------------------------------------------------//
        if(riskScore > 100) riskScore = 100;
        if(riskScore <= 30) fraudAnalysisResult.setStatus(Status.APPROVED);
        if(riskScore > 30 && riskScore <= 70) fraudAnalysisResult.setStatus(Status.SUSPICIOUS);
        if(riskScore > 70 && riskScore <= 100) fraudAnalysisResult.setStatus(Status.BLOCKED);
        //--------------------------------------------------------//
        fraudAnalysisResult.setRiskScore(riskScore);
        fraudAnalysisResult.setReasons(reasons);
        return  fraudAnalysisResult;
    }

}
