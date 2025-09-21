package com.talky.backend.repository.exam;

import com.talky.backend.model.exam.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByExamId(UUID examId);
}
