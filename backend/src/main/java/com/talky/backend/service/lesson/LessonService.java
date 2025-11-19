package com.talky.backend.service.lesson;

import com.talky.backend.model.Course;
import com.talky.backend.model.lesson.Lesson;
import com.talky.backend.repository.lesson.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Lesson updateLesson(UUID id, Lesson lesson) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lección no encontrada"));
        
        if (lesson.getTitle() != null) {
            existingLesson.setTitle(lesson.getTitle());
        }
        if (lesson.getDescription() != null) {
            existingLesson.setDescription(lesson.getDescription());
        }
        if (lesson.getCourse() != null) {
            existingLesson.setCourse(lesson.getCourse());
        }
        
        return lessonRepository.save(existingLesson);
    }

    /**
     * Valida que un profesor sea dueño del curso de una lección.
     */
    public boolean validateCourseOwnership(UUID lessonId, UUID teacherId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lección no encontrada"));
        
        Course course = lesson.getCourse();
        return course != null && 
               course.getTeacher() != null && 
               course.getTeacher().getId().equals(teacherId);
    }

    /**
     * Elimina una lección por su identificador.
     */
    public void delete(UUID id) {
        lessonRepository.deleteById(id);
    }
}
