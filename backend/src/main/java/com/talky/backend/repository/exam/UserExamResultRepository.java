package com.talky.backend.repository;

import com.talky.backend.model.exam.UserExamResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserExamResultRepository extends JpaRepository<UserExamResult, UUID> {

    /**
     * Obtiene todos los resultados de un examen.
     */
    List<UserExamResult> findByExamId(UUID examId);

    /**
     * Obtiene el resultado de un usuario en un examen específico.
     */
    Optional<UserExamResult> findByUserIdAndExamId(UUID userId, UUID examId);
}
