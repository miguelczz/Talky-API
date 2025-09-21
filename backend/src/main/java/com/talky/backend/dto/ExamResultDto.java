package com.talky.backend.dto;

import lombok.Data;
import java.util.Map;

/**
 * DTO para recibir las respuestas de un estudiante al presentar un examen.
 * - La clave del Map es el ID de la pregunta (UUID en String).
 * - El valor es la opción seleccionada (ej. "a", "b", "c").
 */
@Data
public class ExamResultDto {
    private Map<String, String> answers;
}
