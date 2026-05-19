package com.luiz.frauddetection.model.dto.transaction;

import com.luiz.frauddetection.model.Enum.Device;
import com.luiz.frauddetection.model.Enum.Status;
import com.luiz.frauddetection.model.dto.fraud.FraudLogResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@Schema(description = "Resultado do processamento de uma transação")
public class TransactionResponse {

    @Schema(description = "ID gerado para a transação", example = "42")
    private Long id;

    @Schema(description = "Valor da transação", example = "1500.00")
    private BigDecimal amount;

    @Schema(description = "País ou cidade de origem da transação", example = "BR")
    private String location;

    @Schema(description = "Tipo de dispositivo utilizado", example = "MOBILE",
            allowableValues = {"MOBILE", "DESKTOP", "TABLET", "NEW_DEVICE", "UNKNOWN"})
    private Device device;

    @Schema(description = "Data e hora em que a transação foi enviada", example = "2026-05-18T22:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Score de risco atribuído (0 a 100)", example = "85")
    private Integer riskScore;

    @Schema(description = "Status final da transação", example = "BLOCKED",
            allowableValues = {"APPROVED", "SUSPICIOUS", "BLOCKED"})
    private Status status;

    private List<FraudLogResponse> fraudLogs = new ArrayList<>();
}
