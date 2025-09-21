package com.talky.backend.model.chat;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversation_summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationSummary {

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Conversación a la que pertenece el resumen.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    /**
     * Texto del resumen generado automáticamente.
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}