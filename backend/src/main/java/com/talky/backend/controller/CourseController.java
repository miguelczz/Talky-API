package com.talky.backend.controller;

import com.talky.backend.dto.course.CourseRequestDto;
import com.talky.backend.dto.course.CourseResponseDto;
import com.talky.backend.model.Course;
import com.talky.backend.model.User;
import com.talky.backend.service.CourseService;
import com.talky.backend.service.UserService;
import com.talky.backend.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final SecurityUtils securityUtils;

    public CourseController(CourseService courseService, UserService userService, SecurityUtils securityUtils) {
        this.courseService = courseService;
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    /**
     * Crea un nuevo curso.
     * Solo los profesores o administradores pueden crear cursos.
     * El profesor autenticado quedará asignado como responsable del curso.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<CourseResponseDto> createCourse(@Valid @RequestBody CourseRequestDto request) {
        User currentUser = securityUtils.getCurrentUserOrThrow();
        
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        
        // Si es un profesor, lo asignamos automáticamente como teacher del curso
        if (currentUser.getRole() == User.Role.TEACHER) {
            course.setTeacher(currentUser);
        } else if (request.getTeacherId() != null) {
            // Si es ADMIN y especificó un teacherId, lo usamos
            User teacher = userService.getUserById(request.getTeacherId());
            if (teacher.getRole() != User.Role.TEACHER) {
                throw new RuntimeException("El usuario especificado no es un profesor");
            }
            course.setTeacher(teacher);
        } else {
            throw new RuntimeException("Debe especificar un profesor para el curso");
        }
        
        Course savedCourse = courseService.save(course);
        return ResponseEntity.ok(CourseResponseDto.fromCourse(savedCourse));
    }

    /**
     * Obtiene la lista de cursos según el rol del usuario.
     * - Estudiantes: solo ven su curso asignado
     * - Profesores: ven los cursos que dictan
     * - Administradores: ven todos los cursos
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {
        User currentUser = securityUtils.getCurrentUserOrThrow();
        List<Course> courses;

        if (currentUser.getRole() == User.Role.STUDENT) {
            // Estudiantes solo ven su curso asignado
            if (currentUser.getCourseAsStudent() != null) {
                courses = List.of(currentUser.getCourseAsStudent());
            } else {
                courses = List.of();
            }
        } else if (currentUser.getRole() == User.Role.TEACHER) {
            // Profesores ven los cursos que dictan
            courses = courseService.findAll().stream()
                    .filter(course -> course.getTeacher() != null &&
                            course.getTeacher().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        } else {
            // Administradores ven todos los cursos
            courses = courseService.findAll();
        }

        List<CourseResponseDto> dtos = courses.stream()
                .map(CourseResponseDto::fromCourse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Obtiene un curso por su identificador único.
     * Todos los roles tienen acceso.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<CourseResponseDto> getCourseById(@PathVariable UUID id) {
        return courseService.findById(id)
                .map(CourseResponseDto::fromCourse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene un curso por su título.
     * Todos los roles tienen acceso.
     */
    @GetMapping("/title/{title}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<CourseResponseDto> getCourseByTitle(@PathVariable String title) {
        return courseService.findByTitle(title)
                .map(CourseResponseDto::fromCourse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza un curso.
     * Solo los administradores o el profesor dueño del curso pueden hacerlo.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable UUID id,
            @Valid @RequestBody CourseRequestDto request) {
        User currentUser = securityUtils.getCurrentUserOrThrow();
        Course course = courseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Verificar si es profesor y si es dueño del curso
        if (currentUser.getRole() == User.Role.TEACHER) {
            if (course.getTeacher() == null || !course.getTeacher().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
        }

        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getTeacherId() != null && currentUser.getRole() == User.Role.ADMIN) {
            User teacher = userService.getUserById(request.getTeacherId());
            if (teacher.getRole() != User.Role.TEACHER) {
                throw new RuntimeException("El usuario especificado no es un profesor");
            }
            course.setTeacher(teacher);
        }

        Course updatedCourse = courseService.save(course);
        return ResponseEntity.ok(CourseResponseDto.fromCourse(updatedCourse));
    }

    /**
     * Elimina un curso.
     * Solo los administradores o el profesor dueño del curso pueden hacerlo.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        User currentUser = securityUtils.getCurrentUserOrThrow();
        Course course = courseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Verificar si es profesor y si es dueño del curso
        if (currentUser.getRole() == User.Role.TEACHER) {
            if (course.getTeacher() == null || !course.getTeacher().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
        }

        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
