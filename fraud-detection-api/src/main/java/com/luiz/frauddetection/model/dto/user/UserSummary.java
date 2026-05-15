package com.luiz.frauddetection.model.dto.user;

import com.luiz.frauddetection.model.Enum.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary {

    private Long id;
    private String name;
    private String email;
    private Role role;

}
