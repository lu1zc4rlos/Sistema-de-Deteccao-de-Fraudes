package com.luiz.frauddetection.model.dto.user;

import com.luiz.frauddetection.model.dto.transaction.TransactionStatsResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Informações detalhadas de um usuário, visível apenas para administradores")
@Getter @Setter
public class UserAdminResponse {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome do usuário", example = "João Silva")
    private String name;

    @Schema(description = "E-mail do usuário", example = "joao@email.com")
    private String email;

    @Schema(description = "Perfil de acesso", example = "ROLE_USER")
    private String role;

    @Schema(description = "Indica se a conta está bloqueada", example = "false")
    private Boolean isLocked;

    @Schema(description = "Estatísticas das transações do usuário")
    private TransactionStatsResponse stats;
}