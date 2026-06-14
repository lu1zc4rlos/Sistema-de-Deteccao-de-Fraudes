package com.luiz.frauddetection.controller;

import com.luiz.frauddetection.model.dto.user.UserAdminResponse;
import com.luiz.frauddetection.model.dto.user.UserResponse;
import com.luiz.frauddetection.model.dto.user.UserSummary;
import com.luiz.frauddetection.model.dto.user.UserSummaryAdminResponse;
import com.luiz.frauddetection.model.entity.User;
import com.luiz.frauddetection.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<UserSummary> getAllByUser(
            @AuthenticationPrincipal User authenticatedUser) {

        UserResponse userResponse = authService.getUserProfile(authenticatedUser);

        return ResponseEntity.ok(userResponse.getUser());
    }

    @Operation(summary = "Busca um usuário específico (admin)")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado - requer role ADMIN")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserAdminResponse> getByUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User authenticatedUser) {

        return ResponseEntity.ok(authService.getUserDetails(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<UserSummaryAdminResponse>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(authService.getAllUsers(pageable));
    }
}
