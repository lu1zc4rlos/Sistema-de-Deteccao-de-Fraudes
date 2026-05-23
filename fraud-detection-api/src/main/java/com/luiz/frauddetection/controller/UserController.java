package com.luiz.frauddetection.controller;

import com.luiz.frauddetection.model.dto.transaction.TransactionResponse;
import com.luiz.frauddetection.model.dto.user.UserResponse;
import com.luiz.frauddetection.model.dto.user.UserSummary;
import com.luiz.frauddetection.model.entity.User;
import com.luiz.frauddetection.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    /*

   * GET /users/{id} (Busca um usuário específico por ID - para admins)
   * GET /users (Lista todos os usuários - para admins)


     */
}
