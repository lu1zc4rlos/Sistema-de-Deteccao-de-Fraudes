package com.luiz.frauddetection.repository;

import com.luiz.frauddetection.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    <S extends User> S save(S entity);
    Optional<User> findByEmail(String email);
}
