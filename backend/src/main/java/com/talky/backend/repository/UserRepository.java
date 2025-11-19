package com.talky.backend.repository;

import com.talky.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Buscar usuario por cognito_sub o email
    Optional<User> findByCognitoSub(String cognitoSub);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Buscar usuarios por rol
    List<User> findByRole(User.Role role);

    // Buscar estudiantes sin curso asignado
    List<User> findByRoleAndCourseAsStudentIsNull(User.Role role);
}