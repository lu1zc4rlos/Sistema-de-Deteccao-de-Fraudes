package com.luiz.frauddetection.model.dto.transaction;

import com.luiz.frauddetection.model.Enum.Device;
import com.luiz.frauddetection.model.Enum.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Resumo de transação para listagem administrativa")
@Getter @Setter
public class TransactionSummaryAdminResponse {
    private Long id;
    private String userName;
    private String userEmail;
    private BigDecimal amount;
    private String location;
    private Device device;
    private LocalDateTime transactionTime;
    private Integer riskScore;
    private Status status;
}
