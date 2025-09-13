package com.talky.backend.repository;

import com.talky.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Buscar usuario por cognito_sub o email
    Optional<User> findByCognitoSub(String cognitoSub);
    Optional<User> getByEmail(String email);

    boolean existsByEmail(String email);
}