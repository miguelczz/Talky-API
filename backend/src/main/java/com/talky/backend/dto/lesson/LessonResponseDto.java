package com.talky.backend.dto.lesson;

import com.talky.backend.model.lesson.Lesson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO para enviar información de una lección al frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponseDto {
    private UUID id;
    private String title;
    private String description;
    private UUID courseId;
    private String courseTitle;
    private Integer examsCount;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Convierte una entidad Lesson a LessonResponseDto.
     */
    public static LessonResponseDto fromLesson(Lesson lesson) {
        LessonResponseDto.LessonResponseDtoBuilder builder = LessonResponseDto.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt());

        if (lesson.getCourse() != null) {
            builder.courseId(lesson.getCourse().getId())
                   .courseTitle(lesson.getCourse().getTitle());
        }

        if (lesson.getExams() != null) {
            builder.examsCount(lesson.getExams().size());
        } else {
            builder.examsCount(0);
        }

        return builder.build();
    }
}

