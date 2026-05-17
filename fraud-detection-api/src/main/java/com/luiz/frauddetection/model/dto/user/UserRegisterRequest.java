package com.luiz.frauddetection.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class UserRegisterRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email(message = "Email inválido")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "A senha deve conter letras e números")
    private String password;
}
