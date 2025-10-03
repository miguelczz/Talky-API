package com.talky.backend.dto.message;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDto {
    private String type;            // quien contesta (Usuario / IA)
    private String content;         // texto de la IA o del resumen
    private String conversationId;  // conversación a la que pertenece
    private Instant timestamp;      // para pintar en el chat
}
