package com.talky.backend.dto.glossary;

import com.talky.backend.model.GlossaryWord;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO de respuesta para exponer una entrada del glosario al cliente.
 */
public record GlossaryResponseDto(
        UUID id,
        String word,
        String meaning,
        Instant createdAt,
        boolean archived
) {
    /**
     * Convierte una entidad GlossaryWord en este DTO.
     */
    public static GlossaryResponseDto fromEntity(GlossaryWord e) {
        return new GlossaryResponseDto(
                e.getId(),
                e.getWord(),
                e.getMeaning(),
                e.getCreatedAt(),
                e.isArchived()
        );
    }
}
