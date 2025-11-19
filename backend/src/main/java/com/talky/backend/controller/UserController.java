package com.talky.backend.controller;

import com.talky.backend.dto.UserResponseDto;
import com.talky.backend.dto.UserSyncRequest;
import com.talky.backend.model.User;
import com.talky.backend.service.UserService;
import com.talky.backend.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    public UserController(UserService userService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    /**
     * Sincroniza el usuario con la información de Cognito.
     * Este endpoint se usa en login o primera vez que se conecta el usuario.
     */
    @PostMapping("/sync")
    public ResponseEntity<User> syncUser(@RequestBody UserSyncRequest request) {
        return ResponseEntity.ok(userService.syncUser(request));
    }

    /**
     * Obtiene todos los usuarios (solo admin).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Obtiene un usuario por ID (acceso para admin o el mismo usuario).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        User currentUser = securityUtils.getCurrentUserOrThrow();
        User requestedUser = userService.getUserById(id);
        
        // Permitir que el usuario vea su propio perfil o que admin/teacher lo vea
        if (!requestedUser.getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.ADMIN && 
            currentUser.getRole() != User.Role.TEACHER) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(UserResponseDto.fromUser(requestedUser));
    }

    /**
     * Obtiene un usuario por su email (solo admin).
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Optional<User>> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getByEmail(email));
    }

    /**
     * Elimina un usuario (solo admin).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza el rol de un usuario (solo admin).
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRole(@PathVariable UUID id, @RequestParam String role) {
        User.Role newRole = User.Role.valueOf(role.toUpperCase());
        return ResponseEntity.ok(userService.updateUserRole(id, newRole));
    }

    /**
     * Asigna un curso a un estudiante (solo admin o profesor).
     */
    @PutMapping("/{userId}/assign-course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<User> assignCourseToStudent(@PathVariable UUID userId, @PathVariable UUID courseId) {
        return ResponseEntity.ok(userService.assignCourseToStudent(userId, courseId));
    }

    /**
     * Quita el curso de un estudiante (solo admin o profesor).
     */
    @PutMapping("/{userId}/remove-course")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<User> removeCourseFromStudent(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.removeCourseFromStudent(userId));
    }
}
