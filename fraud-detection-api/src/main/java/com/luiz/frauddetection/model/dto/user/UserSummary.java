package com.luiz.frauddetection.model.dto.user;

import com.luiz.frauddetection.model.Enum.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumo simplificado do usuário, usado em listagens e relatórios")
public class UserSummary {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome do usuário", example = "João Silva")
    private String name;

    @Schema(description = "E-mail do usuário", example = "joao@email.com")
    private String email;

    @Schema(description = "Perfil de acesso do usuário", example = "USER",
            allowableValues = {"ROLE_USER", "ROLE_ADMIN", "ROLE_ANALYST"})
    private Role role;

}
