package com.luiz.frauddetection.service;

import com.luiz.frauddetection.config.exception.ConflictException;
import com.luiz.frauddetection.mapper.UserMapper;
import com.luiz.frauddetection.model.dto.user.LoginRequest;
import com.luiz.frauddetection.model.dto.user.UserRegisterRequest;
import com.luiz.frauddetection.model.dto.user.UserResponse;
import com.luiz.frauddetection.model.entity.User;
import com.luiz.frauddetection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

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

}
