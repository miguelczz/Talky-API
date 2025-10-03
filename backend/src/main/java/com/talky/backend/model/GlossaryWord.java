package com.talky.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa una entrada del glosario asociada a un usuario.
 * Nombre de tabla: glossary_words
 */
@Entity
@Table(name = "glossary_words",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_word", columnNames = {"user_id", "word"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlossaryWord {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Relación ManyToOne con User (entidad users existente).
     * Recomiendo mapear a la entidad User en vez de mantener sólo el UUID.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private String meaning;

    @Column(nullable = false)
    private boolean archived = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}