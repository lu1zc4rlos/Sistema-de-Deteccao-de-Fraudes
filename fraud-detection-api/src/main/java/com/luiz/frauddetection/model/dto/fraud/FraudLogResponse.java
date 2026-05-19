package com.luiz.frauddetection.model.dto.fraud;

import com.luiz.frauddetection.model.Enum.FraudReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@Schema(description = "Detalhes do registro de fraude associado a uma transação")
public class FraudLogResponse {

    @Schema(description = "Motivo pelo qual a transação foi sinalizada", example = "Valor acima da média do usuário")
    private FraudReason reason;

    @Schema(description = "Data e hora em que o log foi gerado", example = "2026-05-18T22:00:00")
    private LocalDateTime createdAt;
}
