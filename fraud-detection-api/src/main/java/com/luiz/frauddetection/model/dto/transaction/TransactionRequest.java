package com.luiz.frauddetection.model.dto.transaction;

import com.luiz.frauddetection.model.Enum.Device;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@Schema(description = "Dados enviados pelo cliente para iniciar uma transação")
public class TransactionRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero ")
    @Schema(description = "Valor da transação em reais", example = "1500.00")
    private BigDecimal amount;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$", message = "Deve ser código de país (ex: BR, US)")
    @Schema(description = "País de origem da transação", example = "BR")
    private String location;

    @NotNull
    @Schema(description = "Tipo de dispositivo utilizado", example = "MOBILE",
            allowableValues = {"MOBILE", "DESKTOP", "TABLET", "NEW_DEVICE", "UNKNOWN"})
    private Device device;

}
