package com.luiz.frauddetection.mapper;

import com.luiz.frauddetection.model.Enum.Role;
import com.luiz.frauddetection.model.dto.user.UserRegisterRequest;
import com.luiz.frauddetection.model.dto.user.UserResponse;
import com.luiz.frauddetection.model.dto.user.UserSummary;
import com.luiz.frauddetection.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRegisterRequest request, String passwordHash){

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordHash);
        user.setRole(Role.ROLE_USER);

        return user;
    }

    public UserResponse toResponse(User user,String token,Long expirationTime){

        UserResponse response = new UserResponse();
        response.setAccessToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(expirationTime);
        response.setUser(UserSummary.builder()
                .id(user.getId())

                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build()

        );

        return response;
    }
}
