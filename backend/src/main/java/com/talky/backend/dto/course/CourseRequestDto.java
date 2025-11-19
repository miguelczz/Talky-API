package com.talky.backend.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para crear o actualizar un curso.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDto {

    @NotBlank(message = "El título del curso es requerido")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    private String title;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String description;

    /**
     * ID del profesor (opcional).
     * Solo los administradores pueden especificar un profesor diferente.
     * Si no se proporciona, se usa el usuario autenticado (si es TEACHER).
     */
    private UUID teacherId;
}

