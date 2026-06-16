package com.luiz.frauddetection.controller;

import com.luiz.frauddetection.model.dto.user.*;
import com.luiz.frauddetection.model.entity.User;
import com.luiz.frauddetection.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Usuários", description = "Gerenciamento de perfil e administração de usuários")
public class UserController {

    private final AuthService authService;

    @Operation(
            summary = "Perfil do usuário autenticado",
            description = "Retorna os dados resumidos do usuário autenticado via JWT"
    )
    @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso")
    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou expirado")
    @GetMapping("/me")
    public ResponseEntity<UserSummary> getAllByUser(
            @AuthenticationPrincipal User authenticatedUser) {

        UserResponse userResponse = authService.getUserProfile(authenticatedUser);

        return ResponseEntity.ok(userResponse.getUser());
    }

    @Operation(
            summary = "Buscar usuário por ID (admin)",
            description = "Retorna dados completos de um usuário específico com suas estatísticas de transação. Requer ROLE_ADMIN."
    )
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado — requer ROLE_ADMIN")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserAdminResponse> getByUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User authenticatedUser) {

        return ResponseEntity.ok(authService.getUserDetails(id));
    }

    @Operation(
            summary = "Listar todos os usuários (admin)",
            description = "Retorna lista paginada de todos os usuários com resumo de transações. Requer ROLE_ADMIN."
    )
    @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso negado — requer ROLE_ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<UserSummaryAdminResponse>> getAllUsers(
            Pageable pageable,
            @AuthenticationPrincipal User authenticatedUser) {

        return ResponseEntity.ok(authService.getAllUsers(pageable));
    }

    @Operation(
            summary = "Bloquear ou desbloquear usuário (admin)",
            description = "Atualiza o status de bloqueio da conta de um usuário. Conta bloqueada não consegue realizar login. Requer ROLE_ADMIN."
    )
    @ApiResponse(responseCode = "204", description = "Status atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Campo locked ausente ou inválido")
    @ApiResponse(responseCode = "403", description = "Acesso negado — requer ROLE_ADMIN")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/lock")
    public ResponseEntity<Void> updateUserLockStatus(
            @PathVariable Long id,
            @RequestBody @Valid UserLockRequest request,
            @AuthenticationPrincipal User authenticatedUser) {

        authService.updateUserLockStatus(id,request);
        return ResponseEntity.noContent().build();
    }


}
