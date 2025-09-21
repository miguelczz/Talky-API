package com.talky.backend.controller;

import com.talky.backend.model.lesson.Lesson;
import com.talky.backend.model.User;
import com.talky.backend.model.lesson.UserLesson;
import com.talky.backend.service.lesson.LessonService;
import com.talky.backend.service.lesson.UserLessonService;
import com.talky.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final UserLessonService userLessonService;
    private final UserService userService;

    public LessonController(LessonService lessonService,
                            UserLessonService userLessonService,
                            UserService userService) {
        this.lessonService = lessonService;
        this.userLessonService = userLessonService;
        this.userService = userService;
    }

    /**
     * Crea una nueva lección en un curso.
     * Solo profesores o administradores pueden hacerlo.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        return ResponseEntity.ok(lessonService.save(lesson));
    }

    /**
     * Obtiene todas las lecciones.
     * Acceso permitido a todos los roles.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<List<Lesson>> getAllLessons() {
        return ResponseEntity.ok(lessonService.findAll());
    }

    /**
     * Obtiene una lección por su ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<Lesson> getLessonById(@PathVariable UUID id) {
        return lessonService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene todas las lecciones de un curso específico.
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<List<Lesson>> getLessonsByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(lessonService.findByCourseId(courseId));
    }

    /**
     * Actualiza una lección.
     * Solo profesores o administradores pueden hacerlo.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Lesson> updateLesson(@PathVariable UUID id, @RequestBody Lesson updatedLesson) {
        Optional<Lesson> existingLesson = lessonService.findById(id);
        if (existingLesson.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        updatedLesson.setId(id);
        return ResponseEntity.ok(lessonService.save(updatedLesson));
    }

    /**
     * Elimina una lección.
     * Solo profesores o administradores pueden hacerlo.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Void> deleteLesson(@PathVariable UUID id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Gestión del progreso (UserLesson) ---

    /**
     * Registra o actualiza el progreso de un estudiante en una lección.
     */
    @PostMapping("/{lessonId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<UserLesson> updateProgress(@PathVariable UUID lessonId,
                                                     @RequestParam Integer progress,
                                                     @AuthenticationPrincipal Jwt jwt) {
        String cognitoSub = jwt.getClaim("sub");

        // Buscar usuario autenticado en BD
        User user = userService.getByCognitoSub(cognitoSub)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<UserLesson> existing = userLessonService.findByUserAndLesson(user.getId(), lessonId);

        UserLesson userLesson;
        if (existing.isPresent()) {
            userLesson = existing.get();
            userLesson.setProgress(progress);
            if (progress == 100) {
                userLesson.setCompleted(true);
                userLesson.setCompletedAt(Instant.now());
            }
        } else {
            Lesson lesson = lessonService.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lección no encontrada"));

            userLesson = UserLesson.builder()
                    .user(user)
                    .lesson(lesson)
                    .progress(progress)
                    .completed(progress == 100)
                    .completedAt(progress == 100 ? Instant.now() : null)
                    .build();
        }

        return ResponseEntity.ok(userLessonService.save(userLesson));
    }

    /**
     * Consulta el progreso de un estudiante en una lección específica.
     */
    @GetMapping("/{lessonId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<UserLesson> getProgress(@PathVariable UUID lessonId,
                                                  @AuthenticationPrincipal Jwt jwt) {
        String cognitoSub = jwt.getClaim("sub");

        User user = userService.getByCognitoSub(cognitoSub)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return userLessonService.findByUserAndLesson(user.getId(), lessonId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}