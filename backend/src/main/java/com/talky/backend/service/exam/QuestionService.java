package com.talky.backend.service.exam;

import com.talky.backend.model.exam.Question;
import com.talky.backend.repository.exam.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    /**
     * Obtiene todas las preguntas.
     */
    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    /**
     * Busca una pregunta por su ID.
     */
    public Optional<Question> findById(UUID id) {
        return questionRepository.findById(id);
    }

    /**
     * Obtiene todas las preguntas de un examen.
     */
    public List<Question> findByExamId(UUID examId) {
        return questionRepository.findByExamId(examId);
    }

    /**
     * Crea o actualiza una pregunta.
     */
    public Question save(Question question) {
        return questionRepository.save(question);
    }

    @Transactional
    public Question updateQuestion(UUID id, Question question) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));
        
        if (question.getText() != null) {
            existingQuestion.setText(question.getText());
        }
        if (question.getOptions() != null) {
            existingQuestion.setOptions(question.getOptions());
        }
        if (question.getCorrectAnswer() != null) {
            existingQuestion.setCorrectAnswer(question.getCorrectAnswer());
        }
        if (question.getExam() != null) {
            existingQuestion.setExam(question.getExam());
        }
        
        return questionRepository.save(existingQuestion);
    }

    /**
     * Elimina una pregunta por su ID.
     */
    public void delete(UUID id) {
        questionRepository.deleteById(id);
    }
}