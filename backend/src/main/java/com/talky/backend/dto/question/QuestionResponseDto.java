package com.talky.backend.dto.question;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talky.backend.model.exam.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * DTO para enviar información de una pregunta al frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseDto {
    private UUID id;
    private String text;
    private Map<String, String> options;
    private String correctAnswer;
    private UUID examId;
    private String examTitle;

    /**
     * Convierte una entidad Question a QuestionResponseDto.
     */
    public static QuestionResponseDto fromQuestion(Question question) {
        QuestionResponseDto.QuestionResponseDtoBuilder builder = QuestionResponseDto.builder()
                .id(question.getId())
                .text(question.getText())
                .correctAnswer(question.getCorrectAnswer());

        if (question.getExam() != null) {
            builder.examId(question.getExam().getId())
                   .examTitle(question.getExam().getTitle());
        }

        // Parsear options de JSON string a Map
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> optionsMap = mapper.readValue(
                        question.getOptions(),
                        new TypeReference<Map<String, String>>() {}
                );
                builder.options(optionsMap);
            } catch (Exception e) {
                // Si hay error al parsear, dejar options como null
                builder.options(null);
            }
        }

        return builder.build();
    }
}

