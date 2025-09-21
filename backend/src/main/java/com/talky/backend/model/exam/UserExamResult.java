package com.talky.backend.model.exam;

import com.talky.backend.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_exam_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExamResult {

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Relación con el usuario (estudiante).
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Relación con el examen.
     */
    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    /**
     * Respuestas enviadas por el estudiante en formato JSON.
     * Ejemplo:
     * {
     *   "q1": "a",
     *   "q2": "c",
     *   "q3": "d"
     * }
     */
    @Column(columnDefinition = "jsonb")
    private String answers;

    /**
     * Calificación obtenida (0.0 - 100.0).
     */
    @Column(nullable = false)
    private Double score;

    /**
     * Marca cuándo se presentó el examen.
     */
    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private Instant submittedAt;
}
