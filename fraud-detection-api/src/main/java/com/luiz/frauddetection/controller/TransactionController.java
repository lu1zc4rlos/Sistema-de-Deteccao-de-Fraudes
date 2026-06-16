package com.luiz.frauddetection.controller;

import com.luiz.frauddetection.model.dto.transaction.TransactionRequest;
import com.luiz.frauddetection.model.dto.transaction.TransactionResponse;
import com.luiz.frauddetection.model.dto.transaction.TransactionSummaryAdminResponse;
import com.luiz.frauddetection.model.entity.User;
import com.luiz.frauddetection.service.TransactionService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import com.luiz.frauddetection.model.dto.transaction.TransactionStatsResponse;
import org.springframework.web.bind.annotation.*;

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

    @Operation(
            summary = "Estatísticas do usuário",
            description = "Retorna estatísticas consolidadas das transações do usuário autenticado"
    )
    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou expirado")
    @GetMapping("/stats")
    public ResponseEntity<TransactionStatsResponse> getStats(@AuthenticationPrincipal User authenticatedUser) {

        TransactionStatsResponse transactionStatsResponse = transactionService.getUserStats(authenticatedUser);

        return ResponseEntity.ok(transactionStatsResponse);
    }

    @Operation(
            summary = "Listar transações do usuário",
            description = "Retorna todas as transações do usuário autenticado com seus fraud logs"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou expirado")
    @GetMapping("/")
    public ResponseEntity<List<TransactionResponse>> getAllByUser(
            @AuthenticationPrincipal User authenticatedUser) {

        List<TransactionResponse> transactions = transactionService.getUserTransactions(authenticatedUser);

        return ResponseEntity.ok(transactions);
    }

    @Operation(
            summary = "Detalhe de uma transação",
            description = "Retorna os dados completos de uma transação específica do usuário autenticado"
    )
    @ApiResponse(responseCode = "200", description = "Transação encontrada")
    @ApiResponse(responseCode = "403", description = "Transação não pertence ao usuário autenticado")
    @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getByUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User authenticatedUser) {

        TransactionResponse transactionResponse = transactionService
                .getUserTransaction(authenticatedUser, id);

        return ResponseEntity.ok(transactionResponse);
    }

    @Operation(
            summary = "Listar todas as transações (admin)",
            description = "Retorna todas as transações do sistema de forma paginada. Requer ROLE_ADMIN."
    )
    @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso negado — requer ROLE_ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<TransactionSummaryAdminResponse>> getAllTransactions(
            Pageable pageable,
            @AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(transactionService.getAllTransactions(pageable));
    }

    @Operation(
            summary = "Detalhe de qualquer transação (admin)",
            description = "Retorna os dados completos de qualquer transação pelo ID, sem restrição de dono. Requer ROLE_ADMIN."
    )
    @ApiResponse(responseCode = "200", description = "Transação encontrada")
    @ApiResponse(responseCode = "403", description = "Acesso negado — requer ROLE_ADMIN")
    @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/admin")
    public ResponseEntity<TransactionResponse> getUserTransactionAdmin(
            @PathVariable Long id,
            @AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(transactionService.getUserTransactionAdmin(id));
    }

}
