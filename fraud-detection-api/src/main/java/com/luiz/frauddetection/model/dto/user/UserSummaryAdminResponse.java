package com.luiz.frauddetection.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Resumo de usuário para listagem administrativa")
@Getter @Setter
public class UserSummaryAdminResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Boolean isLocked;
    private Long totalTransactions;
    private Long flaggedTransactions;
}
