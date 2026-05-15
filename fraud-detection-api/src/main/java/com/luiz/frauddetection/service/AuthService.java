package com.luiz.frauddetection.service;

import com.luiz.frauddetection.mapper.UserMapper;
import com.luiz.frauddetection.model.dto.user.LoginRequest;
import com.luiz.frauddetection.model.dto.user.UserRegisterRequest;
import com.luiz.frauddetection.model.dto.user.UserResponse;
import com.luiz.frauddetection.model.entity.User;
import com.luiz.frauddetection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Value("${jwt.expiration}")
    private Long expirationTime;

    public UserResponse register(UserRegisterRequest request){

        String hashDaSenha = passwordEncoder.encode(request.getPassword());

        User user = userMapper.toEntity(request,hashDaSenha);
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        UserResponse response = userMapper.toResponse(user,token,expirationTime);

        return response;
    }

    public UserResponse login(LoginRequest request){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                ));

        var user = (User) authentication.getPrincipal();

        String token = jwtService.generateToken(user);
        UserResponse response = userMapper.toResponse(user,token,expirationTime);

        return response;
    }

}
