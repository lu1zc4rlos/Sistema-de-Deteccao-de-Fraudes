package com.luiz.frauddetection.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@Schema(description = "Dados para criação de uma nova conta")
public class UserRegisterRequest {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String name;

    @NotBlank
    @Email(message = "Email inválido")
    @Schema(description = "E-mail para login e contato", example = "joao@email.com")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "A senha deve conter letras e números")
    @Schema(description = "Senha de acesso", example = "Senha123.")
    private String password;
}
