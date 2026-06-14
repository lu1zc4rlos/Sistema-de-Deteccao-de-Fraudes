package com.luiz.frauddetection.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Requisição para alterar o status de bloqueio de um usuário")
@Getter @Setter
public class UserLockRequest {

    @Schema(description = "true para bloquear, false para desbloquear", example = "true")
    @NotNull(message = "O campo locked é obrigatório")
    private Boolean locked;
}
