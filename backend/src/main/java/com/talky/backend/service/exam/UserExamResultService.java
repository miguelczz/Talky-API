package com.talky.backend.service.exam;

import com.talky.backend.model.exam.UserExamResult;
import com.talky.backend.repository.UserExamResultRepository;
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
        return userExamResultRepository.findByExamId(examId);
    }

    /**
     * Obtiene el resultado de un usuario en un examen específico.
     */
    public Optional<UserExamResult> findByUserAndExam(UUID userId, UUID examId) {
        return userExamResultRepository.findByUserIdAndExamId(userId, examId);
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
}
