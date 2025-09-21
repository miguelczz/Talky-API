package com.talky.backend.controller.chat;

import com.talky.backend.model.User;
import com.talky.backend.model.chat.Conversation;
import com.talky.backend.service.UserService;
import com.talky.backend.service.chat.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final UserService userService;

    public ConversationController(ConversationService conversationService, UserService userService) {
        this.conversationService = conversationService;
        this.userService = userService;
    }

    /**
     * Crea una nueva conversación.
     * El modo depende del rol del usuario (STUDENT o TEACHER).
     */
    @PostMapping
    public ResponseEntity<Conversation> createConversation(
            @AuthenticationPrincipal Jwt principal,
            @RequestParam(required = false) String title) {

        String sub = principal.getClaim("sub");
        User user = userService.getByCognitoSub(sub)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Usamos el rol del usuario como modo de conversación
        String mode = user.getRole().name();

        Conversation conversation = conversationService.createConversation(user, title, mode);
        return ResponseEntity.ok(conversation);
    }

    /**
     * Obtiene todas las conversaciones del usuario autenticado.
     */
    @GetMapping
    public ResponseEntity<List<Conversation>> getAllConversations(
            @AuthenticationPrincipal Jwt principal) {

        String sub = principal.getClaim("sub");
        User user = userService.getByCognitoSub(sub)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(conversationService.getAllByUser(user));
    }

    /**
     * Elimina una conversación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable UUID id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.noContent().build();
    }
}