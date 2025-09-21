package com.talky.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    // Sub de Cognito para identificar al usuario
    @Column(nullable = false, unique = true, name = "cognito_sub")
    private String cognitoSub;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // STUDENT, TEACHER, ADMIN

    @Column(name = "phone_number")
    private String phoneNumber;

    private String birthdate;

    private String gender;

    // Relación: un profesor puede dictar varios cursos
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Course> coursesAsTeacher;

    // Relación: un estudiante puede estar inscrito en un solo curso
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course courseAsStudent;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum Role {
        STUDENT,
        TEACHER,
        ADMIN
    }
}
