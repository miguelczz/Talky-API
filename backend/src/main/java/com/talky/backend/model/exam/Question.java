package com.talky.backend.model.exam;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String text;

    /**
     * Opciones de respuesta en formato JSONB.
     * Ejemplo:
     * {
     *   "a": "Opción 1",
     *   "b": "Opción 2",
     *   "c": "Opción 3",
     *   "d": "Opción 4"
     * }
     */
    @Column(columnDefinition = "jsonb")
    private String options;

    /**
     * Respuesta correcta (clave de la opción, por ejemplo "a").
     */
    @Column(nullable = false)
    private String correctAnswer;

    // Relación con el examen
    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;
}
