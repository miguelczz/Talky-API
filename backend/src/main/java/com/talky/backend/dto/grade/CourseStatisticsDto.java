package com.talky.backend.dto.grade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO para estadísticas de un curso.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseStatisticsDto {
    private UUID courseId;
    private String courseTitle;
    private Integer totalStudents;
    private Integer totalLessons;
    private Integer totalExams;
    private Double averageScore;
    private List<ExamStatisticsDto> examStatistics;
}

