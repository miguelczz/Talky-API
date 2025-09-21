package com.talky.backend.service.lesson;

import com.talky.backend.model.lesson.Lesson;
import com.talky.backend.repository.lesson.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    /**
     * Obtiene todas las lecciones.
     */
    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    /**
     * Busca una lección por su identificador.
     */
    public Optional<Lesson> findById(UUID id) {
        return lessonRepository.findById(id);
    }

    /**
     * Busca todas las lecciones de un curso específico.
     */
    public List<Lesson> findByCourseId(UUID courseId) {
        return lessonRepository.findByCourseId(courseId);
    }

    /**
     * Guarda o actualiza una lección.
     */
    public Lesson save(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    /**
     * Elimina una lección por su identificador.
     */
    public void delete(UUID id) {
        lessonRepository.deleteById(id);
    }
}
