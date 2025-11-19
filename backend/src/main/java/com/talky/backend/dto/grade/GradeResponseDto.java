package com.talky.backend.dto.grade;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talky.backend.model.exam.UserExamResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO para enviar información de una calificación (resultado de examen).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeResponseDto {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private String studentEmail;
    private UUID examId;
    private String examTitle;
    private UUID lessonId;
    private String lessonTitle;
    private UUID courseId;
    private String courseTitle;
    private Double score;
    private Map<String, String> answers;
    private Instant submittedAt;

    /**
     * Convierte una entidad UserExamResult a GradeResponseDto.
     */
    public static GradeResponseDto fromUserExamResult(UserExamResult result) {
        GradeResponseDto.GradeResponseDtoBuilder builder = GradeResponseDto.builder()
                .id(result.getId())
                .score(result.getScore())
                .submittedAt(result.getSubmittedAt());

        if (result.getUser() != null) {
            builder.studentId(result.getUser().getId())
                   .studentName(result.getUser().getName())
                   .studentEmail(result.getUser().getEmail());
        }

        if (result.getExam() != null) {
            builder.examId(result.getExam().getId())
                   .examTitle(result.getExam().getTitle());

            if (result.getExam().getLesson() != null) {
                builder.lessonId(result.getExam().getLesson().getId())
                       .lessonTitle(result.getExam().getLesson().getTitle());

                if (result.getExam().getLesson().getCourse() != null) {
                    builder.courseId(result.getExam().getLesson().getCourse().getId())
                           .courseTitle(result.getExam().getLesson().getCourse().getTitle());
                }
            }
        }

        // Parsear answers de JSON string a Map
        if (result.getAnswers() != null && !result.getAnswers().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> answersMap = mapper.readValue(
                        result.getAnswers(),
                        new TypeReference<Map<String, String>>() {}
                );
                builder.answers(answersMap);
            } catch (Exception e) {
                // Si hay error al parsear, dejar answers como null
                builder.answers(null);
            }
        }

        return builder.build();
    }
}

