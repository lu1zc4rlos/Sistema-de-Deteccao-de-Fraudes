package com.luiz.frauddetection.model.dto.user;

import com.luiz.frauddetection.model.Enum.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String nome;
    private String email;
    private Role role;

}
