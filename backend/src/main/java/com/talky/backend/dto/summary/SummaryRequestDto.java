package com.talky.backend.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar historial de conversación a N8N.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryRequestDto {
    private String history;
}
