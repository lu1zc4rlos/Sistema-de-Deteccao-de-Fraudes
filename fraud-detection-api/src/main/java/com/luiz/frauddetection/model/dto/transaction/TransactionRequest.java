package com.luiz.frauddetection.model.dto.transaction;

import com.luiz.frauddetection.model.Enum.Device;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
public class TransactionRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero ")
    private BigDecimal amount;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$", message = "Deve ser código de país (ex: BR, US)")
    private String location;

    @NotNull
    private Device device;

}
