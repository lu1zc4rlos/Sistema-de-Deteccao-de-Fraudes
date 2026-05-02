package com.luiz.frauddetection.model.dto.fraudAnalysis;

import com.luiz.frauddetection.model.Enum.FraudReason;
import com.luiz.frauddetection.model.Enum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class FraudAnalysisResult {

    private Integer riskScore;
    private Status status;
    private List<FraudReason> reasons;
}
