package com.talky.backend.controller;

import com.talky.backend.model.exam.Exam;
import com.talky.backend.model.exam.Question;
import com.talky.backend.model.exam.UserExamResult;
import com.talky.backend.model.User;
import com.talky.backend.service.exam.ExamService;
import com.talky.backend.service.exam.QuestionService;
import com.talky.backend.service.exam.UserExamResultService;
import com.talky.backend.service.lesson.LessonService;
import com.talky.backend.dto.ExamResultDto;
import com.talky.backend.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;
    private final QuestionService questionService;
    private final LessonService lessonService;
    private final UserExamResultService userExamResultService;
    private final SecurityUtils securityUtils;

    public ExamController(
            ExamService examService,
            QuestionService questionService,
            LessonService lessonService,
            UserExamResultService userExamResultService,
            SecurityUtils securityUtils
    ) {
        this.examService = examService;
        this.questionService = questionService;
        this.lessonService = lessonService;
        this.userExamResultService = userExamResultService;
        this.securityUtils = securityUtils;
    }

    // --- Gestión de exámenes ---

    @PostMapping("/lesson/{lessonId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Exam> createExam(@PathVariable UUID lessonId, @RequestBody Exam exam) {
        exam.setLesson(lessonService.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lección no encontrada")));
        return ResponseEntity.ok(examService.save(exam));
    }

    /**
     * Obtiene todos los exámenes.
     * - Estudiantes: solo ven exámenes de su curso
     * - Profesores: ven exámenes de sus cursos
     * - Administradores: ven todos los exámenes
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<List<Exam>> getAllExams() {
        User currentUser = securityUtils.getCurrentUserOrThrow();
        List<Exam> exams;

        if (currentUser.getRole() == User.Role.STUDENT) {
            // Estudiantes solo ven exámenes de su curso
            if (currentUser.getCourseAsStudent() == null) {
                exams = List.of();
            } else {
                exams = examService.findAll().stream()
                        .filter(exam -> exam.getLesson() != null &&
                                exam.getLesson().getCourse() != null &&
                                exam.getLesson().getCourse().getId().equals(currentUser.getCourseAsStudent().getId()))
                        .collect(Collectors.toList());
            }
        } else if (currentUser.getRole() == User.Role.TEACHER) {
            // Profesores ven exámenes de sus cursos
            exams = examService.findAll().stream()
                    .filter(exam -> exam.getLesson() != null &&
                            exam.getLesson().getCourse() != null &&
                            exam.getLesson().getCourse().getTeacher() != null &&
                            exam.getLesson().getCourse().getTeacher().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        } else {
            // Administradores ven todos los exámenes
            exams = examService.findAll();
        }

        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<Exam> getExamById(@PathVariable UUID id) {
        return examService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<List<Exam>> getExamsByLesson(@PathVariable UUID lessonId) {
        return ResponseEntity.ok(examService.findByLessonId(lessonId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Void> deleteExam(@PathVariable UUID id) {
        examService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Gestión de preguntas ---

    @PostMapping("/{examId}/questions")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Question> addQuestion(@PathVariable UUID examId, @RequestBody Question question) {
        question.setExam(examService.findById(examId)
                .orElseThrow(() -> new RuntimeException("Examen no encontrado")));
        return ResponseEntity.ok(questionService.save(question));
    }

    @GetMapping("/{examId}/questions")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<List<Question>> getQuestionsByExam(@PathVariable UUID examId) {
        return ResponseEntity.ok(questionService.findByExamId(examId));
    }

    @GetMapping("/questions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<Question> getQuestionById(@PathVariable UUID id) {
        return questionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/questions/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Gestión de resultados (UserExamResult) ---

    /**
     * Un estudiante envía sus respuestas de un examen.
     */

    @PostMapping("/{examId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<UserExamResult> submitExam(
            @PathVariable UUID examId,
            @RequestBody ExamResultDto submission
    ) {
        User student = securityUtils.getCurrentUserOrThrow();

        Exam exam = examService.findById(examId)
                .orElseThrow(() -> new RuntimeException("Examen no encontrado"));

        // Obtener respuestas enviadas
        Map<String, String> answersMap = submission.getAnswers();
        if (answersMap == null) {
            throw new RuntimeException("No se recibieron respuestas");
        }

        // --- Calcular score ---
        List<Question> questions = questionService.findByExamId(examId);
        int totalQuestions = questions.size();
        int correctCount = 0;

        for (Question q : questions) {
            String answer = answersMap.get(q.getId().toString());
            if (answer != null && answer.equalsIgnoreCase(q.getCorrectAnswer())) {
                correctCount++;
            }
        }

        double score = totalQuestions > 0 ? (correctCount * 100.0 / totalQuestions) : 0.0;

        // Guardar en DB
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String answersJson;
        try {
            answersJson = mapper.writeValueAsString(answersMap);
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar respuestas", e);
        }

        UserExamResult result = userExamResultService.findByUserAndExam(student.getId(), examId)
                .orElse(UserExamResult.builder().user(student).exam(exam).build());

        result.setAnswers(answersJson);
        result.setScore(score);

        return ResponseEntity.ok(userExamResultService.save(result));
    }

    /**
     * Profesor/Admin obtiene todos los resultados de un examen.
     * - Profesores: solo ven resultados de exámenes de sus cursos
     * - Administradores: ven todos los resultados
     */
    @GetMapping("/{examId}/results")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<List<UserExamResult>> getResultsByExam(@PathVariable UUID examId) {
        User currentUser = securityUtils.getCurrentUserOrThrow();
        List<UserExamResult> results = userExamResultService.findByExamId(examId);

        if (currentUser.getRole() == User.Role.TEACHER) {
            // Profesores solo ven resultados de exámenes de sus cursos
            Exam exam = examService.findById(examId)
                    .orElseThrow(() -> new RuntimeException("Examen no encontrado"));
            
            if (exam.getLesson() == null || exam.getLesson().getCourse() == null ||
                    exam.getLesson().getCourse().getTeacher() == null ||
                    !exam.getLesson().getCourse().getTeacher().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
        }

        return ResponseEntity.ok(results);
    }

    /**
     * Profesor/Admin obtiene el resultado de un estudiante en un examen.
     */
    @GetMapping("/{examId}/results/{userId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<UserExamResult> getResultByUser(
            @PathVariable UUID examId,
            @PathVariable UUID userId
    ) {
        return userExamResultService.findByUserAndExam(userId, examId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
