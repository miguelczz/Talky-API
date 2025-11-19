package com.talky.backend.repository.exam;

import com.talky.backend.model.exam.UserExamResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserExamResultRepository extends JpaRepository<UserExamResult, UUID> {

    /**
     * Obtiene todos los resultados de un examen.
     */
    List<UserExamResult> findByExam_Id(UUID examId);

    /**
     * Obtiene el resultado de un usuario en un examen específico.
     */
    Optional<UserExamResult> findByUser_IdAndExam_Id(UUID userId, UUID examId);

    /**
     * Obtiene todos los resultados de un usuario.
     */
    List<UserExamResult> findByUser_Id(UUID userId);
}
