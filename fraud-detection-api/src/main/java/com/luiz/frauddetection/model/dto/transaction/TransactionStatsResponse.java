package com.luiz.frauddetection.model.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estatísticas consolidadas das transações do usuário")
public class TransactionStatsResponse {

    @Schema(description = "Total de transações realizadas", example = "23")
    private long totalTransactions;

    @Schema(description = "Soma total dos valores transacionados em reais", example = "15750.00")
    private BigDecimal totalAmount;

    @Schema(description = "Média do score de risco de todas as transações (0 a 100)", example = "34.5")
    private double averageRiskScore;

    @Schema(description = "Total de transações aprovadas", example = "18")
    private long approvedCount;

    @Schema(description = "Total de transações marcadas como suspeitas", example = "3")
    private long suspiciousCount;

    @Schema(description = "Total de transações bloqueadas", example = "2")
    private long blockedCount;
}
