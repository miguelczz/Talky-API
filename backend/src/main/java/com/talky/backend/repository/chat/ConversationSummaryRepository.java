package com.talky.backend.repository.chat;

import com.talky.backend.model.chat.Conversation;
import com.talky.backend.model.chat.Message;
import com.talky.backend.model.chat.MessageType;
import com.talky.backend.model.chat.ConversationSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationSummaryRepository extends JpaRepository<ConversationSummary, UUID> {

    /**
     * Obtiene todos los resúmenes de una conversación, ordenados por fecha.
     */
    List<ConversationSummary> findByConversationOrderByCreatedAtAsc(Conversation conversation);

    /**
     * Borrar resúmenes previos de una conversación
     */
    void deleteByConversation(Conversation conversation);

    /**
     * Obtener el último resumen (opcional, ya usado por getLatestSummary)
     */
    Optional<ConversationSummary> findTopByConversationOrderByCreatedAtDesc(Conversation conversation);
}
