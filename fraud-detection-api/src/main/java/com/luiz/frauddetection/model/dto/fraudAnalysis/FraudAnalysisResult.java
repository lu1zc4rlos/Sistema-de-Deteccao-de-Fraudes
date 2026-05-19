package com.luiz.frauddetection.model.dto.fraudAnalysis;

import com.luiz.frauddetection.model.Enum.FraudReason;
import com.luiz.frauddetection.model.Enum.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
@Schema(description = "Resultado retornado pelo motor de análise de fraude")
public class FraudAnalysisResult {

    @Schema(description = "Score de risco calculado entre 0 (seguro) e 100 (fraude)", example = "85")
    private Integer riskScore;

    @Schema(description = "Decisão final da análise", example = "BLOCKED")
    private Status status;

    @Schema(description = "Razões principais que motivaram a decisão", example = " HIGH_AMOUNT, UNUSUAL_LOCATION")
    private List<FraudReason> reasons;
}
