package com.talky.backend.service.exam;

import com.talky.backend.model.exam.Exam;
import com.talky.backend.repository.exam.ExamRepository;
import org.springframework.stereotype.Service;

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

    /**
     * Elimina un examen por su ID.
     */
    public void delete(UUID id) {
        examRepository.deleteById(id);
    }
}
