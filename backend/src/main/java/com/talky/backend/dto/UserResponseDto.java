package com.talky.backend.dto;

import com.talky.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO para enviar información del usuario al frontend.
 * Incluye el rol para que el frontend pueda decidir qué mostrar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private UUID id;
    private String email;
    private String name;
    private User.Role role;  // Rol del usuario: STUDENT, TEACHER, ADMIN
    private String phoneNumber;
    private String birthdate;
    private String gender;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Información adicional según el rol
    private UUID courseId;  // Para estudiantes: ID del curso asignado
    private String courseTitle;  // Para estudiantes: título del curso
    private Integer coursesCount;  // Para profesores: cantidad de cursos que dicta

    /**
     * Convierte una entidad User a UserResponseDto.
     */
    public static UserResponseDto fromUser(User user) {
        UserResponseDto.UserResponseDtoBuilder builder = UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .birthdate(user.getBirthdate())
                .gender(user.getGender())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt());

        // Agregar información específica según el rol
        if (user.getRole() == User.Role.STUDENT && user.getCourseAsStudent() != null) {
            builder.courseId(user.getCourseAsStudent().getId());
            builder.courseTitle(user.getCourseAsStudent().getTitle());
        }

        if (user.getRole() == User.Role.TEACHER && user.getCoursesAsTeacher() != null) {
            builder.coursesCount(user.getCoursesAsTeacher().size());
        }

        return builder.build();
    }
}

