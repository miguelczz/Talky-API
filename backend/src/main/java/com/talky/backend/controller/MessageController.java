package com.talky.backend.controller;

import com.talky.backend.dto.message.MessageRequestDto;
import com.talky.backend.dto.message.MessageResponseDto;
import com.talky.backend.model.User;
import com.talky.backend.model.chat.Conversation;
import com.talky.backend.model.chat.Message;
import com.talky.backend.service.UserService;
import com.talky.backend.service.chat.ConversationService;
import com.talky.backend.service.chat.MessageService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final ConversationService conversationService;

    // Mapa en memoria para manejar rate limiting por usuario
    private final Map<UUID, Bucket> buckets = new ConcurrentHashMap<>();

    public MessageController(MessageService messageService,
                             UserService userService,
                             ConversationService conversationService) {
        this.messageService = messageService;
        this.userService = userService;
        this.conversationService = conversationService;
    }

    /**
     * Envía un mensaje dentro de una conversación y devuelve la respuesta de la IA.
     */
    @PostMapping("/{conversationId}")
    public ResponseEntity<MessageResponseDto> sendMessage(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable UUID conversationId,
            @RequestBody Map<String, String> request) {

        String sub = principal.getClaim("sub");
        User user = userService.getByCognitoSub(sub)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Rate limiting por rol
        Bucket bucket = getBucketForUser(user);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(429).body(
                    MessageResponseDto.builder()
                            .respuesta("Has excedido el límite de mensajes permitidos.")
                            .conversationId(conversationId.toString())
                            .timestamp(Instant.now())
                            .build()
            );
        }

        String userMessage = request.get("message");

        // Construir DTO limpio para el service
        MessageRequestDto dto = MessageRequestDto.builder()
                .studentEmail(user.getEmail())
                .prompt(userMessage)
                .conversationId(conversationId.toString())
                .build();

        // Delegar al service
        MessageResponseDto response = messageService.handleMessage(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene el historial reducido (últimos N mensajes) de una conversación.
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<List<MessageResponseDto>> getConversationHistory(
            @PathVariable UUID conversationId) {

        Conversation conversation = conversationService.getById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));

        List<Message> messages = messageService.getRecentMessages(conversation);

        // Mapear entidad -> DTO
        List<MessageResponseDto> response = messages.stream()
                .map(m -> MessageResponseDto.builder()
                        .respuesta(m.getContent())
                        .conversationId(conversationId.toString())
                        .timestamp(m.getCreatedAt())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Configura el bucket de rate limiting según el rol del usuario.
     */
    private Bucket getBucketForUser(User user) {
        return buckets.computeIfAbsent(user.getId(), id -> {
            Bandwidth limit;

            switch (user.getRole()) {
                case TEACHER:
                    limit = Bandwidth.classic(200, Refill.greedy(200, Duration.ofHours(1)));
                    break;
                case STUDENT:
                    limit = Bandwidth.classic(30, Refill.greedy(30, Duration.ofHours(1)));
                    break;
                case ADMIN:
                    limit = Bandwidth.classic(1000, Refill.greedy(1000, Duration.ofHours(1)));
                    break;
                default:
                    limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofHours(1)));
            }

            return Bucket.builder().addLimit(limit).build();
        });
    }
}
