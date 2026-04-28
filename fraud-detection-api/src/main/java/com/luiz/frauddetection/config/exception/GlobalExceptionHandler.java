package com.luiz.frauddetection.config.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiError buildError(
            HttpStatus status,
            String message,
            String path,
            List<String> details
    ) {
        return new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                message,
                path,
                details
        );
    }

    private ApiError buildError(
            HttpStatus status,
            String message,
            String path
    ) {
        return buildError(status, message, path, List.of());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildError(
                        HttpStatus.NOT_FOUND,
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildError(
                        HttpStatus.CONFLICT,
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessRuleException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                buildError(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiError> handleExternal(ExternalServiceException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                buildError(
                        HttpStatus.BAD_GATEWAY,
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildError(
                        HttpStatus.UNAUTHORIZED,
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                buildError(
                        HttpStatus.FORBIDDEN,
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethod(MethodArgumentNotValidException ex, HttpServletRequest request){

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError(
                        HttpStatus.BAD_REQUEST,
                        "Erro de validação",
                        request.getRequestURI(),
                        details
                )
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request){

        List<String> details = List.of(
                "Verifique campos únicos como email ou dados obrigatórios"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildError(
                        HttpStatus.CONFLICT,
                        "Violação de integridade de dados",
                        request.getRequestURI(),
                        details
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Ocorreu um erro interno inesperado no servidor.",
                        request.getRequestURI()
                )
        );
    }
}
