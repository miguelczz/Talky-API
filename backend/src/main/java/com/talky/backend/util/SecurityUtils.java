package com.talky.backend.util;

import com.talky.backend.model.User;
import com.talky.backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utilidades para trabajar con seguridad y obtener información del usuario autenticado.
 */
@Component
public class SecurityUtils {

    private final UserService userService;

    public SecurityUtils(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene el JWT del contexto de seguridad actual.
     */
    public Optional<Jwt> getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return Optional.of(jwtAuth.getToken());
        }
        return Optional.empty();
    }

    /**
     * Obtiene el cognitoSub del usuario autenticado.
     */
    public Optional<String> getCurrentUserCognitoSub() {
        return getCurrentJwt()
                .map(jwt -> jwt.getClaimAsString("sub"));
    }

    /**
     * Obtiene el usuario completo desde la base de datos usando el JWT actual.
     */
    public Optional<User> getCurrentUser() {
        return getCurrentUserCognitoSub()
                .flatMap(userService::getByCognitoSub);
    }

    /**
     * Obtiene el usuario actual o lanza una excepción si no está autenticado.
     */
    public User getCurrentUserOrThrow() {
        return getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
    }

    /**
     * Verifica si el usuario actual tiene un rol específico.
     */
    public boolean hasRole(User.Role role) {
        return getCurrentUser()
                .map(user -> user.getRole() == role)
                .orElse(false);
    }

    /**
     * Verifica si el usuario actual es un estudiante.
     */
    public boolean isStudent() {
        return hasRole(User.Role.STUDENT);
    }

    /**
     * Verifica si el usuario actual es un profesor.
     */
    public boolean isTeacher() {
        return hasRole(User.Role.TEACHER);
    }

    /**
     * Verifica si el usuario actual es un administrador.
     */
    public boolean isAdmin() {
        return hasRole(User.Role.ADMIN);
    }

    /**
     * Verifica si el usuario actual es profesor o administrador.
     */
    public boolean isTeacherOrAdmin() {
        return isTeacher() || isAdmin();
    }
}

