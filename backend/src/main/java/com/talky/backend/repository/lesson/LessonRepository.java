package com.talky.backend.repository.lesson;

import com.talky.backend.model.lesson.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    /**
     * Busca todas las lecciones de un curso específico.
     */
    List<Lesson> findByCourseId(UUID courseId);
}
