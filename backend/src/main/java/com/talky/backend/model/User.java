package com.talky.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
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

    @Column(nullable = false, unique = true, name = "cognito_sub")
    private String cognitoSub;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    private String role;

    @Column(name = "created_at", updatable = false, insertable = false,
            columnDefinition = "timestamptz default now()")
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false,
            columnDefinition = "timestamptz default now()")
    private Instant updatedAt;
}
