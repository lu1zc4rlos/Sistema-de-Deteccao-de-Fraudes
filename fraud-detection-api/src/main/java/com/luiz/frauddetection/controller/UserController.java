package com.luiz.frauddetection.controller;

import com.luiz.frauddetection.model.dto.user.LoginRequest;
import com.luiz.frauddetection.model.dto.user.UserRegisterRequest;
import com.luiz.frauddetection.model.dto.user.UserResponse;
import com.luiz.frauddetection.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints públicos para login e registro de usuários")
public class UserController {

    private final AuthService authService;

    @Operation(
            summary = "Login do usuário",
            description = "Autentica o usuário com e-mail e senha, retornando o token JWT e os dados do usuário"
    )
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request){
        UserResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Registro de novo usuário",
            description = "Cria uma nova conta de usuário e retorna o token JWT para acesso imediato"
    )
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já cadastrado")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> cadastro(@Valid @RequestBody UserRegisterRequest request){
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
