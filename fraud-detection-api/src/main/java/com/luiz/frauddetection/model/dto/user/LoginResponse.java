package com.luiz.frauddetection.model.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private UserResponse user;
}
