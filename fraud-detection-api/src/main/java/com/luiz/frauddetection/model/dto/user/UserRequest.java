package com.luiz.frauddetection.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {

    @NotBlank
    @Email(message = "Email inválido")
    private String email;

    @NotBlank
    private String senha;
}
