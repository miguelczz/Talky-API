package com.talky.backend.dto.exam;

import com.talky.backend.model.exam.Exam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO para enviar información de un examen al frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResponseDto {
    private UUID id;
    private String title;
    private String description;
    private UUID lessonId;
    private String lessonTitle;
    private UUID courseId;
    private String courseTitle;
    private Integer questionsCount;
    private Double averageScore;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Convierte una entidad Exam a ExamResponseDto.
     */
    public static ExamResponseDto fromExam(Exam exam) {
        ExamResponseDto.ExamResponseDtoBuilder builder = ExamResponseDto.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt());

        if (exam.getLesson() != null) {
            builder.lessonId(exam.getLesson().getId())
                   .lessonTitle(exam.getLesson().getTitle());

            if (exam.getLesson().getCourse() != null) {
                builder.courseId(exam.getLesson().getCourse().getId())
                       .courseTitle(exam.getLesson().getCourse().getTitle());
            }
        }

        if (exam.getQuestions() != null) {
            builder.questionsCount(exam.getQuestions().size());
        } else {
            builder.questionsCount(0);
        }

        return builder.build();
    }
}

