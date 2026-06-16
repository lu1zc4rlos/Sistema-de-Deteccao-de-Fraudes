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

    @Schema(description = "ID da transação", example = "42")
    private Long id;

    @Schema(description = "Nome do usuário que realizou a transação", example = "João Silva")
    private String userName;

    @Schema(description = "E-mail do usuário que realizou a transação", example = "joao@email.com")
    private String userEmail;

    @Schema(description = "Valor da transação em reais", example = "1500.00")
    private BigDecimal amount;

    @Schema(description = "País de origem da transação", example = "BR")
    private String location;

    @Schema(description = "Dispositivo utilizado", example = "MOBILE",
            allowableValues = {"MOBILE", "DESKTOP", "TABLET", "NEW_DEVICE", "UNKNOWN"})
    private Device device;

    @Schema(description = "Data e hora da transação", example = "2026-05-18T22:00:00")
    private LocalDateTime transactionTime;

    @Schema(description = "Score de risco calculado (0 a 100)", example = "85")
    private Integer riskScore;

    @Schema(description = "Status final da transação", example = "BLOCKED",
            allowableValues = {"APPROVED", "SUSPICIOUS", "BLOCKED"})
    private Status status;
}
