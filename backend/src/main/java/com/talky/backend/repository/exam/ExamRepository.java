package com.talky.backend.repository.exam;

import com.talky.backend.model.exam.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {
    List<Exam> findByLessonId(UUID lessonId);
}
