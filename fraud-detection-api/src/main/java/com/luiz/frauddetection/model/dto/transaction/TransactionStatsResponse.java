package com.luiz.frauddetection.model.dto.transaction;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatsResponse {
    private long totalTransactions;
    private BigDecimal totalAmount;
    private double averageRiskScore;
    private long approvedCount;
    private long suspiciousCount;
    private long blockedCount;
}
