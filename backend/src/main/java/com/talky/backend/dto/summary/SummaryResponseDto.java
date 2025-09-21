package com.talky.backend.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir el resumen de N8N.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponseDto {
    private String summary;
}
