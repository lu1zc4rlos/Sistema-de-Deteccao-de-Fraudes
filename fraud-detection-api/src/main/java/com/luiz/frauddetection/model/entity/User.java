package com.luiz.frauddetection.model.entity;

import com.luiz.frauddetection.model.Enum.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @JoinColumn(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false, length = 100)
    @Getter @Setter
    private String name;

    @Getter @Setter
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Getter @Setter
    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

}
