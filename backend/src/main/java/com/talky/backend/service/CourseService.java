package com.talky.backend.service;

import com.talky.backend.model.Course;
import com.talky.backend.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(UUID id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> findByTitle(String title) {
        return courseRepository.findByTitle(title);
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(UUID id, Course course) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        if (course.getTitle() != null) {
            existingCourse.setTitle(course.getTitle());
        }
        if (course.getDescription() != null) {
            existingCourse.setDescription(course.getDescription());
        }
        if (course.getTeacher() != null) {
            existingCourse.setTeacher(course.getTeacher());
        }
        
        return courseRepository.save(existingCourse);
    }

    /**
     * Valida que un profesor sea dueño de un curso.
     */
    public boolean validateTeacherOwnership(UUID courseId, UUID teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        return course.getTeacher() != null && 
               course.getTeacher().getId().equals(teacherId);
    }

    public void delete(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        // Opcional: Validar que no tenga estudiantes asignados
        if (course.getStudents() != null && !course.getStudents().isEmpty()) {
            throw new RuntimeException("No se puede eliminar un curso con estudiantes asignados");
        }
        
        courseRepository.deleteById(id);
    }
}
