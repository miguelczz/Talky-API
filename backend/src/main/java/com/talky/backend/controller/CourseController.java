package com.talky.backend.controller;

import com.talky.backend.model.Course;
import com.talky.backend.model.User;
import com.talky.backend.service.CourseService;
import com.talky.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    /**
     * Crea un nuevo curso.
     * Solo los profesores o administradores pueden crear cursos.
     * El profesor autenticado quedará asignado como responsable del curso.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Course> createCourse(@RequestBody Course course,
                                               @AuthenticationPrincipal Jwt jwt) {
        // Si es un profesor, lo asignamos automáticamente como teacher del curso
        if (jwt.getClaimAsStringList("cognito:groups").contains("TEACHER")) {
            String cognitoSub = jwt.getClaim("sub");
            User teacher = userService.getByCognitoSub(cognitoSub)
                    .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
            course.setTeacher(teacher);
        }
        return ResponseEntity.ok(courseService.save(course));
    }

    /**
     * Obtiene la lista de todos los cursos.
     * Todos los roles tienen acceso.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.findAll());
    }

    /**
     * Obtiene un curso por su identificador único.
     * Todos los roles tienen acceso.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<Course> getCourseById(@PathVariable UUID id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene un curso por su título.
     * Todos los roles tienen acceso.
     */
    @GetMapping("/title/{title}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<Course> getCourseByTitle(@PathVariable String title) {
        return courseService.findByTitle(title)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un curso.
     * Solo los administradores o el profesor dueño del curso pueden hacerlo.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id,
                                             @AuthenticationPrincipal Jwt jwt) {
        Course course = courseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Verificar si es profesor y si es dueño del curso
        if (jwt.getClaimAsStringList("cognito:groups").contains("TEACHER")) {
            String cognitoSub = jwt.getClaim("sub");
            if (!course.getTeacher().getCognitoSub().equals(cognitoSub)) {
                return ResponseEntity.status(403).build();
            }
        }

        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
