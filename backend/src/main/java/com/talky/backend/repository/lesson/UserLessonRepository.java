package com.talky.backend.repository.lesson;

import com.talky.backend.model.lesson.UserLesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserLessonRepository extends JpaRepository<UserLesson, UUID> {

    /**
     * Busca el progreso de un usuario en una lección.
     */
    Optional<UserLesson> findByUserIdAndLessonId(UUID userId, UUID lessonId);
}
