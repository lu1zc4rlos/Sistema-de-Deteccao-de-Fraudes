package com.luiz.frauddetection.model.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private UserSummary user;
}
