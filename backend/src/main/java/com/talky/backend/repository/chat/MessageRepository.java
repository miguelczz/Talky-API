package com.talky.backend.repository.chat;

import com.talky.backend.model.chat.Conversation;
import com.talky.backend.model.chat.Message;
import com.talky.backend.model.chat.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    /**
     * Obtiene todos los mensajes de una conversación ordenados por fecha.
     */
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);

    /**
     * Obtiene los últimos N mensajes de una conversación.
     */
    List<Message> findTop50ByConversationOrderByCreatedAtDesc(Conversation conversation);

    /**
     * Buscar mensajes de tipo SUMMARY para una conversación (para borrarlos)
     */
    List<Message> findByConversationAndType(Conversation conversation, MessageType type);

    /**
     * Elimina todos los mensajes de una conversación.
     */
    void deleteByConversation(Conversation conversation);
}