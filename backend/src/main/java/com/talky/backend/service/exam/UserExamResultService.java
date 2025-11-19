package com.talky.backend.service.exam;

import com.talky.backend.model.exam.UserExamResult;
import com.talky.backend.repository.exam.UserExamResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserExamResultService {

    private final UserExamResultRepository userExamResultRepository;

    public UserExamResultService(UserExamResultRepository userExamResultRepository) {
        this.userExamResultRepository = userExamResultRepository;
    }

    /**
     * Guarda un resultado de examen.
     */
    public UserExamResult save(UserExamResult result) {
        return userExamResultRepository.save(result);
    }

    /**
     * Obtiene todos los resultados de un examen.
     */
    public List<UserExamResult> findByExamId(UUID examId) {
        return userExamResultRepository.findByExam_Id(examId);
    }

    /**
     * Obtiene el resultado de un usuario en un examen específico.
     */
    public Optional<UserExamResult> findByUserAndExam(UUID userId, UUID examId) {
        return userExamResultRepository.findByUser_IdAndExam_Id(userId, examId);
    }

    /**
     * Obtiene todos los resultados de un usuario.
     */
    public List<UserExamResult> findByUserId(UUID userId) {
        return userExamResultRepository.findByUser_Id(userId);
    }

    /**
     * Obtiene un resultado por su ID.
     */
    public Optional<UserExamResult> findById(UUID id) {
        return userExamResultRepository.findById(id);
    }

    /**
     * Elimina un resultado por su ID.
     */
    public void delete(UUID id) {
        userExamResultRepository.deleteById(id);
    }

    /**
     * Calcula el promedio de calificaciones de un estudiante en un curso.
     */
    public Double calculateAverageScore(UUID userId, UUID courseId) {
        List<UserExamResult> results = findByUserId(userId);
        
        return results.stream()
                .filter(result -> result.getExam() != null &&
                        result.getExam().getLesson() != null &&
                        result.getExam().getLesson().getCourse() != null &&
                        result.getExam().getLesson().getCourse().getId().equals(courseId))
                .mapToDouble(UserExamResult::getScore)
                .average()
                .orElse(0.0);
    }

    /**
     * Calcula la tasa de aprobación de un examen (score >= 70).
     */
    public Double getPassRate(UUID examId) {
        List<UserExamResult> results = findByExamId(examId);
        if (results.isEmpty()) {
            return 0.0;
        }
        
        long passedCount = results.stream()
                .filter(result -> result.getScore() >= 70.0)
                .count();
        
        return (passedCount * 100.0) / results.size();
    }

    /**
     * Calcula el promedio de calificaciones de un examen.
     */
    public Double getAverageScore(UUID examId) {
        List<UserExamResult> results = findByExamId(examId);
        if (results.isEmpty()) {
            return 0.0;
        }
        
        return results.stream()
                .mapToDouble(UserExamResult::getScore)
                .average()
                .orElse(0.0);
    }
}
