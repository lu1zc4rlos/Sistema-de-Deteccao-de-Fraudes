package com.luiz.frauddetection.model.dto.transaction;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter @Setter
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
