package com.talky.backend.service.exam;

import com.talky.backend.model.Course;
import com.talky.backend.model.exam.Exam;
import com.talky.backend.model.lesson.Lesson;
import com.talky.backend.repository.exam.ExamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    /**
     * Obtiene todos los exámenes.
     */
    public List<Exam> findAll() {
        return examRepository.findAll();
    }

    /**
     * Busca un examen por su ID.
     */
    public Optional<Exam> findById(UUID id) {
        return examRepository.findById(id);
    }

    /**
     * Busca todos los exámenes de una lección.
     */
    public List<Exam> findByLessonId(UUID lessonId) {
        return examRepository.findByLessonId(lessonId);
    }

    /**
     * Crea o actualiza un examen.
     */
    public Exam save(Exam exam) {
        return examRepository.save(exam);
    }

    @Transactional
    public Exam updateExam(UUID id, Exam exam) {
        Exam existingExam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen no encontrado"));
        
        if (exam.getTitle() != null) {
            existingExam.setTitle(exam.getTitle());
        }
        if (exam.getDescription() != null) {
            existingExam.setDescription(exam.getDescription());
        }
        if (exam.getLesson() != null) {
            existingExam.setLesson(exam.getLesson());
        }
        
        return examRepository.save(existingExam);
    }

    /**
     * Valida que un profesor sea dueño de la lección de un examen.
     */
    public boolean validateLessonOwnership(UUID examId, UUID teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Examen no encontrado"));
        
        Lesson lesson = exam.getLesson();
        if (lesson == null) {
            return false;
        }
        
        Course course = lesson.getCourse();
        return course != null && 
               course.getTeacher() != null && 
               course.getTeacher().getId().equals(teacherId);
    }

    /**
     * Elimina un examen por su ID.
     */
    public void delete(UUID id) {
        examRepository.deleteById(id);
    }
}
