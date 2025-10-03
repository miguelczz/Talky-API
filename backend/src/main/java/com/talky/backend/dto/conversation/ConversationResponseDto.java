package com.talky.backend.dto.conversation;

import com.talky.backend.model.chat.Conversation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ConversationResponseDto {
    private UUID id;
    private String title;
    private UUID userId; // solo lo necesario, no todo el User completo

    public static ConversationResponseDto fromEntity(Conversation conversation) {
        return new ConversationResponseDto(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getUser() != null ? conversation.getUser().getId() : null
        );
    }
}
