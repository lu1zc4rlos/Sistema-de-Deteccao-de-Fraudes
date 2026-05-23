package com.luiz.frauddetection.controller;

import com.luiz.frauddetection.model.dto.transaction.TransactionRequest;
import com.luiz.frauddetection.model.dto.transaction.TransactionResponse;
import com.luiz.frauddetection.model.dto.user.UserSummary;
import com.luiz.frauddetection.model.entity.User;
import com.luiz.frauddetection.repository.UserRepository;
import com.luiz.frauddetection.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Criação e análise de transações financeiras em tempo real")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            summary = "Criar nova transação",
            description = "Processa uma transação financeira, calcula o score de risco e retorna o resultado da análise de fraude. Requer autenticação JWT."
    )
    @ApiResponse(responseCode = "201", description = "Transação processada — aprovada, suspeita ou bloqueada")
    @ApiResponse(responseCode = "400", description = "Dados da transação inválidos")
    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou expirado")
    @ApiResponse(responseCode = "403", description = "Usuário bloqueado por suspeita de fraude")
    @PostMapping("/create")
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody @Valid TransactionRequest request,
            @AuthenticationPrincipal User authenticatedUser) {

        TransactionResponse response = transactionService
                .createTransaction(request,authenticatedUser);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<TransactionResponse>> getAllByUser(
            @AuthenticationPrincipal User authenticatedUser) {

        List<TransactionResponse> transactions = transactionService.getUserTransactions(authenticatedUser);

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getByUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User authenticatedUser) {

        TransactionResponse transactionResponse = transactionService
                .getUserTransaction(authenticatedUser, id);

        return ResponseEntity.ok(transactionResponse);
    }

}
