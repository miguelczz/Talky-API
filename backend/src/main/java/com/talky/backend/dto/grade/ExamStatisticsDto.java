package com.talky.backend.dto.grade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para estadísticas de un examen.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamStatisticsDto {
    private UUID examId;
    private String examTitle;
    private Integer totalSubmissions;
    private Double averageScore;
    private Double passRate; // Porcentaje de aprobados (score >= 70)
    private Double highestScore;
    private Double lowestScore;
}

