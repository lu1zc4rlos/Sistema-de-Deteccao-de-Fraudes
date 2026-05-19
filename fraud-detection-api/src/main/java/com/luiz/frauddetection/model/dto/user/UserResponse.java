package com.luiz.frauddetection.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Dados do usuário retornados pela API")
public class UserResponse {

    @Schema(description = "Token JWT para autenticação nas requisições", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Tipo do token", example = "Bearer")
    private String tokenType;

    @Schema(description = "Tempo de expiração do token em segundos", example = "86400")
    private Long expiresIn;

    @Schema(description = "Dados resumidos do usuário autenticado")
    private UserSummary user;
}
