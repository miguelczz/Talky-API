package com.talky.backend.service.chat;

import com.talky.backend.model.User;
import com.talky.backend.model.chat.Conversation;
import com.talky.backend.repository.chat.ConversationRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;

    // Límite máximo de conversaciones permitidas por usuario.
    private static final int MAX_CONVERSATIONS = 4;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    /**
     * Crea una nueva conversación para el usuario,
     * siempre que no supere el límite permitido.
     */
    @Transactional
    public Conversation createConversation(User user, String title, String mode) {
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        long conversationCount = conversationRepository.countByEmail(user.getEmail());

        if (conversationCount >= MAX_CONVERSATIONS) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Has alcanzado el límite de conversaciones permitidas"
            );
        }

        Conversation conversation = Conversation.builder()
                .user(user)
                .title(title != null ? title : "Nueva conversación")
                .mode(mode)
                .build();

        return conversationRepository.save(conversation);
    }

    /**
     * Obtiene todas las conversaciones de un usuario.
     */
    public List<Conversation> getAllByUser(User user) {
        return conversationRepository.findByUser(user);
    }

    /**
     * Obtiene una conversación por su ID.
     */
    public Optional<Conversation> getById(UUID id) {
        return conversationRepository.findById(id);
    }

    /**
     * Actualiza el título de una conversación.
     */
    @Transactional
    public Conversation updateTitle(UUID id, String newTitle) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada con id: " + id));

        // Evitar títulos duplicados para el mismo usuario
        if (conversationRepository.existsByUserAndTitle(conversation.getUser(), newTitle)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una conversación con ese título"
            );
        }

        conversation.setTitle(newTitle);
        return conversationRepository.save(conversation);
    }

    /**
     * Elimina una conversación definitivamente.
     */
    @Transactional
    public void deleteConversation(UUID id) {
        conversationRepository.deleteById(id);
    }
}
