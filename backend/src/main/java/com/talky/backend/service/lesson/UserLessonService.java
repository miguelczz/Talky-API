package com.talky.backend.service.lesson;

import com.talky.backend.model.lesson.UserLesson;
import com.talky.backend.repository.lesson.UserLessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserLessonService {

    private final UserLessonRepository userLessonRepository;

    public UserLessonService(UserLessonRepository userLessonRepository) {
        this.userLessonRepository = userLessonRepository;
    }

    /**
     * Guarda o actualiza el progreso de un usuario en una lección.
     */
    public UserLesson save(UserLesson userLesson) {
        return userLessonRepository.save(userLesson);
    }

    /**
     * Busca el progreso de un usuario en una lección específica.
     */
    public Optional<UserLesson> findByUserAndLesson(UUID userId, UUID lessonId) {
        return userLessonRepository.findByUserIdAndLessonId(userId, lessonId);
    }

    /**
     * Obtiene todos los registros de progreso.
     */
    public List<UserLesson> findAll() {
        return userLessonRepository.findAll();
    }

    /**
     * Elimina un registro de progreso.
     */
    public void delete(UUID id) {
        userLessonRepository.deleteById(id);
    }
}
