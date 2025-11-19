package com.talky.backend.controller;

import com.talky.backend.dto.UpdateProfileRequest;
import com.talky.backend.dto.UpdateRoleRequest;
import com.talky.backend.dto.UserResponseDto;
import com.talky.backend.model.Course;
import com.talky.backend.model.User;
import com.talky.backend.service.CourseService;
import com.talky.backend.service.UserService;
import com.talky.backend.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador específico para administradores.
 * Todos los endpoints requieren rol ADMIN.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final SecurityUtils securityUtils;
    private final UserService userService;
    private final CourseService courseService;

    public AdminController(
            SecurityUtils securityUtils,
            UserService userService,
            CourseService courseService
    ) {
        this.securityUtils = securityUtils;
        this.userService = userService;
        this.courseService = courseService;
    }

    /**
     * Obtiene el perfil del administrador autenticado.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getProfile() {
        User admin = securityUtils.getCurrentUserOrThrow();
        return ResponseEntity.ok(UserResponseDto.fromUser(admin));
    }

    /**
     * Actualiza el perfil del administrador autenticado.
     * Solo puede actualizar su propio perfil.
     */
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDto> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User currentUser = securityUtils.getCurrentUserOrThrow();
        User updatedUser = userService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(UserResponseDto.fromUser(updatedUser));
    }

    /**
     * Obtiene todos los usuarios del sistema.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDto> userDtos = users.stream()
                .map(UserResponseDto::fromUser)
                .toList();
        return ResponseEntity.ok(userDtos);
    }

    /**
     * Obtiene usuarios por rol.
     * Ejemplo: /api/admin/users/by-role?role=STUDENT
     */
    @GetMapping("/users/by-role")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@RequestParam User.Role role) {
        List<User> users = userService.getUsersByRole(role);
        List<UserResponseDto> userDtos = users.stream()
                .map(UserResponseDto::fromUser)
                .toList();
        return ResponseEntity.ok(userDtos);
    }

    /**
     * Obtiene estudiantes sin curso asignado.
     */
    @GetMapping("/users/students-without-course")
    public ResponseEntity<List<UserResponseDto>> getStudentsWithoutCourse() {
        List<User> students = userService.getStudentsWithoutCourse();
        List<UserResponseDto> userDtos = students.stream()
                .map(UserResponseDto::fromUser)
                .toList();
        return ResponseEntity.ok(userDtos);
    }

    /**
     * Obtiene un usuario por ID.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserResponseDto.fromUser(user));
    }

    /**
     * Actualiza el perfil de cualquier usuario del sistema.
     * Permite a los administradores gestionar los datos de todos los usuarios.
     * No permite modificar email ni role desde este endpoint.
     */
    @PutMapping("/users/{userId}/profile")
    public ResponseEntity<UserResponseDto> updateUserProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        User updatedUser = userService.updateProfile(userId, request);
        return ResponseEntity.ok(UserResponseDto.fromUser(updatedUser));
    }

    /**
     * Actualiza el rol de un usuario.
     * Usa un DTO para mejor validación.
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        User updatedUser = userService.updateUserRole(id, request.getRole());
        return ResponseEntity.ok(UserResponseDto.fromUser(updatedUser));
    }

    /**
     * Actualiza el rol de un usuario (versión alternativa con query param para compatibilidad).
     */
    @PutMapping("/users/{id}/role-simple")
    public ResponseEntity<UserResponseDto> updateUserRoleSimple(
            @PathVariable UUID id,
            @RequestParam String role
    ) {
        try {
            User.Role newRole = User.Role.valueOf(role.toUpperCase());
            User updatedUser = userService.updateUserRole(id, newRole);
            return ResponseEntity.ok(UserResponseDto.fromUser(updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Elimina un usuario.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene todos los cursos del sistema.
     */
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.findAll());
    }

    /**
     * Asigna un curso a un estudiante.
     */
    @PutMapping("/users/{userId}/assign-course/{courseId}")
    public ResponseEntity<User> assignCourseToStudent(
            @PathVariable UUID userId,
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(userService.assignCourseToStudent(userId, courseId));
    }

    /**
     * Quita el curso de un estudiante.
     */
    @PutMapping("/users/{userId}/remove-course")
    public ResponseEntity<User> removeCourseFromStudent(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.removeCourseFromStudent(userId));
    }
}

