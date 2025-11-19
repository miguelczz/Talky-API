package com.talky.backend.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO para crear o actualizar una pregunta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequestDto {

    @NotBlank(message = "El texto de la pregunta es requerido")
    @Size(max = 2000, message = "El texto no puede exceder 2000 caracteres")
    private String text;

    /**
     * Opciones de respuesta en formato Map.
     * Ejemplo: {"a": "Opción 1", "b": "Opción 2", "c": "Opción 3", "d": "Opción 4"}
     */
    @NotNull(message = "Las opciones son requeridas")
    @Size(min = 2, message = "Debe haber al menos 2 opciones")
    private Map<String, String> options;

    @NotBlank(message = "La respuesta correcta es requerida")
    private String correctAnswer;
}

