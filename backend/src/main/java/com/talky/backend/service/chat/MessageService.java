package com.talky.backend.service.chat;

import com.talky.backend.dto.message.MessageRequestDto;
import com.talky.backend.dto.message.MessageResponseDto;
import com.talky.backend.model.User;
import com.talky.backend.model.chat.*;
import com.talky.backend.repository.UserRepository;
import com.talky.backend.repository.chat.ConversationRepository;
import com.talky.backend.repository.chat.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationSummaryService conversationSummaryService;
    private final UserRepository userRepository;

    // Límite máximo de conversaciones por usuario
    private static final int MAX_CONVERSATIONS = 4;

    // Máximo de mensajes antes de resumir
    private static final int MAX_MESSAGES_PER_CONVERSATION = 20;

    // Control de bloqueo por conversación (para evitar mensajes simultáneos)
    private final Map<UUID, Boolean> conversationLocks = new ConcurrentHashMap<>();

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Maneja el flujo completo de un mensaje:
     *  - Verifica/crea conversación
     *  - Evita mensajes simultáneos en la misma conversación
     *  - Guarda mensaje del usuario
     *  - Llama a N8N para respuesta IA
     *  - Guarda respuesta de la IA
     *  - Aplica lógica de resúmenes si supera el límite de mensajes
     */
    public MessageResponseDto handleMessage(MessageRequestDto request) {
        // 1. Obtener el usuario a partir del email
        User user = userRepository.findByEmail(request.getStudentEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 2. Verificar si ya alcanzó el límite de conversaciones
        long conversationCount = conversationRepository.countByEmail(user.getEmail());
        Conversation conversation;

        if (request.getConversationId() == null || request.getConversationId().isEmpty()) {
            if (conversationCount >= MAX_CONVERSATIONS) {
                throw new RuntimeException("Has alcanzado el límite de conversaciones permitidas (" + MAX_CONVERSATIONS + ")");
            }
            // Crear nueva conversación
            conversation = Conversation.builder()
                    .user(user)
                    .title("Nueva conversación")
                    .mode(user.getRole().name()) // usar enum Role -> String
                    .build();
            conversation = conversationRepository.save(conversation);
        } else {
            conversation = conversationRepository.findById(UUID.fromString(request.getConversationId()))
                    .orElseThrow(() -> new IllegalArgumentException("Conversación no encontrada"));
        }

        UUID conversationId = conversation.getId();

        // 3. Verificar si la conversación ya está en uso
        if (Boolean.TRUE.equals(conversationLocks.get(conversationId))) {
            throw new RuntimeException("Ya tienes un mensaje en proceso en esta conversación. Espera la respuesta de la IA.");
        }

        try {
            // Bloquear conversación
            conversationLocks.put(conversationId, true);

            // 4. Guardar mensaje del usuario
            Message userMessage = Message.builder()
                    .conversation(conversation)
                    .type(MessageType.USER)
                    .content(request.getPrompt())
                    .build();
            messageRepository.save(userMessage);

            // 5. Enviar mensaje a N8N
            String respuesta = callN8n(request);

            // 6. Guardar respuesta de la IA
            Message aiMessage = Message.builder()
                    .conversation(conversation)
                    .type(MessageType.AI)
                    .content(respuesta)
                    .build();
            messageRepository.save(aiMessage);

            // 7. Verificar si hay que resumir
            List<Message> messages = messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
            if (messages.size() > MAX_MESSAGES_PER_CONVERSATION) {
                String resumen = conversationSummaryService.generateSummary(messages);

                // Guardar resumen en tabla de summaries
                conversationSummaryService.saveSummary(conversation, resumen);

                // Guardar resumen como mensaje especial
                conversationSummaryService.saveSummaryAsMessage(conversation, resumen);

                // Mantener solo el resumen y últimos N mensajes
                List<Message> recent = messageRepository.findTop50ByConversationOrderByCreatedAtDesc(conversation);
                messageRepository.deleteAll(
                        messages.stream()
                                .filter(m -> !recent.contains(m) && m.getType() != MessageType.SUMMARY)
                                .toList()
                );
            }

            // 8. Retornar DTO para el front
            return MessageResponseDto.builder()
                    .respuesta(respuesta)
                    .conversationId(conversationId.toString())
                    .timestamp(aiMessage.getCreatedAt())
                    .build();

        } finally {
            // Liberar conversación siempre, incluso si ocurre un error
            conversationLocks.remove(conversationId);
        }
    }

    /**
     * Obtiene los últimos N mensajes de una conversación, incluyendo resúmenes.
     */
    public List<Message> getRecentMessages(Conversation conversation) {
        List<Message> recent = messageRepository.findTop50ByConversationOrderByCreatedAtDesc(conversation);
        Collections.reverse(recent);
        return recent;
    }

    /**
     * Llama al webhook de N8N y devuelve la respuesta como String.
     */
    private String callN8n(MessageRequestDto request) {
        try {
            String url = "http://localhost:5678/webhook/talky-ia";
            MessageResponseDto response = restTemplate.postForObject(url, request, MessageResponseDto.class);
            return response != null ? response.getRespuesta() : "No se recibió respuesta de la IA";
        } catch (Exception e) {
            return "Error al conectar con el servicio de IA";
        }
    }
}