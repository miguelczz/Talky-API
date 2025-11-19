package com.talky.backend.dto.grade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO para enviar todas las calificaciones de un estudiante.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGradesDto {
    private UUID studentId;
    private String studentName;
    private String studentEmail;
    private List<GradeResponseDto> grades;
    private Double averageScore;
    private Integer totalExams;
    private Integer passedExams; // Exámenes con score >= 70
}

