package com.talky.backend.model.lesson;

import com.talky.backend.model.exam.Exam;
import com.talky.backend.model.Course;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    // Relación con el curso
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Relación con exámenes
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<Exam> exams;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
