package com.talky.backend.model.lesson;

import com.talky.backend.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLesson {

    @Id
    @GeneratedValue
    private UUID id;

    // Relación con usuario
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relación con lección
    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    // Estado de progreso
    private Integer progress; // porcentaje 0–100
    private Boolean completed;
    private Instant completedAt;
}
