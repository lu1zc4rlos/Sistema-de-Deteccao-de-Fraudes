package com.luiz.frauddetection.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Credenciais para autenticação do usuário")
public class LoginRequest {

    @NotBlank
    @Email(message = "Email inválido")
    @Schema(description = "E-mail cadastrado", example = "usuario@email.com")
    private String email;

    @NotBlank
    @Schema(description = "Senha do usuário", example = "Senha123.")
    private String password;
}
