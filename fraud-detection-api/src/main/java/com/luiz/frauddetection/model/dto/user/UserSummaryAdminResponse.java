package com.luiz.frauddetection.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Resumo de usuário para listagem administrativa")
@Getter @Setter
public class UserSummaryAdminResponse {
    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome do usuário", example = "João Silva")
    private String name;

    @Schema(description = "E-mail do usuário", example = "joao@email.com")
    private String email;

    @Schema(description = "Perfil de acesso do usuário", example = "ROLE_USER",
            allowableValues = {"ROLE_USER", "ROLE_ADMIN"})
    private String role;

    @Schema(description = "Indica se a conta está bloqueada", example = "false")
    private Boolean isLocked;

    @Schema(description = "Total de transações realizadas pelo usuário", example = "23")
    private Long totalTransactions;

    @Schema(description = "Total de transações bloqueadas ou suspeitas", example = "2")
    private Long flaggedTransactions;
}
