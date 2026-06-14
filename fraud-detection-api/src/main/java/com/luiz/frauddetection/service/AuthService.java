package com.luiz.frauddetection.service;

import com.luiz.frauddetection.config.exception.ConflictException;
import com.luiz.frauddetection.config.exception.ResourceNotFoundException;
import com.luiz.frauddetection.mapper.UserMapper;
import com.luiz.frauddetection.model.dto.transaction.TransactionStatsResponse;
import com.luiz.frauddetection.model.dto.user.*;
import com.luiz.frauddetection.model.entity.Transaction;
import com.luiz.frauddetection.model.entity.User;
import com.luiz.frauddetection.repository.TransactionRepository;
import com.luiz.frauddetection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TransactionService transactionService;

    @Value("${api.security.token.expiration}")
    private Long expirationTime;

    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Este e-mail já está cadastrado.");
            }

            String hashDaSenha = passwordEncoder.encode(request.getPassword());

            User user = userMapper.toEntity(request, hashDaSenha);
            user = userRepository.save(user);

            String token = jwtService.generateToken(user);

            return userMapper.toResponse(user, token, expirationTime);

        } catch (DataIntegrityViolationException e) {
            System.err.println("Erro de integridade no banco de dados:");
            e.printStackTrace();
            throw e;

        } catch (Exception e) {
            System.err.println("Erro inesperado no processo de registro:");
            e.printStackTrace();
            throw e;
        }
    }

    public UserResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();

            String token = jwtService.generateToken(user);

            return userMapper.toResponse(user, token, expirationTime);

        } catch (BadCredentialsException e) {
            System.err.println("Erro de autenticação: E-mail ou senha inválidos.");
            throw e;

        } catch (LockedException e) {
            System.err.println("Erro de autenticação: Esta conta está bloqueada.");
            throw e;

        } catch (AuthenticationException e) {
            System.err.println("Erro geral de segurança no login:");
            e.printStackTrace();
            throw e;

        } catch (Exception e) {
            System.err.println("Erro inesperado no processo de login:");
            e.printStackTrace();
            throw e;
        }
    }

    public UserResponse getUserProfile(User authenticatedUser){

        User user = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return userMapper.toResponse(user,null,null);
    }

    public UserAdminResponse getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        TransactionStatsResponse stats = transactionService.getUserStats(user);

        return userMapper.toAdminResponse(user, stats);
    }

    public Page<UserSummaryAdminResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        return users.map(user -> {
            List<Transaction> transactions = transactionRepository.findByUser(user);
            return userMapper.toSummaryAdminResponse(user, transactions);
        });
    }

}
