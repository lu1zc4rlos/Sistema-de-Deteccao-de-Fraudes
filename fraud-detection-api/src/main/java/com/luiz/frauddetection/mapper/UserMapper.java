package com.luiz.frauddetection.mapper;

import com.luiz.frauddetection.model.Enum.Role;
import com.luiz.frauddetection.model.dto.transaction.TransactionStatsResponse;
import com.luiz.frauddetection.model.dto.user.*;
import com.luiz.frauddetection.model.entity.Transaction;
import com.luiz.frauddetection.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public UserAdminResponse toAdminResponse(User user, TransactionStatsResponse stats){

        UserAdminResponse userAdminResponse = new UserAdminResponse();
        userAdminResponse.setId(user.getId());
        userAdminResponse.setName(user.getName());
        userAdminResponse.setEmail(user.getEmail());
        userAdminResponse.setRole(String.valueOf(user.getRole()));
        userAdminResponse.setIsLocked(user.getIsLocked());
        userAdminResponse.setStats(stats);

        return userAdminResponse;
    }

    public UserSummaryAdminResponse toSummaryAdminResponse(User user, List<Transaction> transactions) {
        UserSummaryAdminResponse response = new UserSummaryAdminResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(String.valueOf(user.getRole()));
        response.setIsLocked(user.getIsLocked());
        response.setTotalTransactions((long) transactions.size());
        response.setFlaggedTransactions(
                transactions.stream()
                        .filter(t -> !"APPROVED".equals(t.getStatus()))
                        .count()
        );

        return response;
    }
}
