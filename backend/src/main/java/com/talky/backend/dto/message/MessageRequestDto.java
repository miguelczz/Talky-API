package com.talky.backend.dto.message;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequestDto {
    private String studentEmail;   // quién envía
    private String prompt;         // mensaje del usuario
    private String conversationId; // opcional: si es un nuevo chat, puede venir vacío
}
