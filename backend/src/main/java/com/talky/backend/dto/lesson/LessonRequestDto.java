package com.talky.backend.dto.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para crear o actualizar una lección.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequestDto {

    @NotBlank(message = "El título de la lección es requerido")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    private String title;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String description;

    @NotNull(message = "El ID del curso es requerido")
    private UUID courseId;
}

