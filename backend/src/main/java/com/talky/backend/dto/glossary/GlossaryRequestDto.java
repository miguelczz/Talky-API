package com.talky.backend.dto.glossary;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de petición para crear una entrada en el glosario.
 */
public record GlossaryRequestDto(
        @NotBlank String word,
        @NotBlank String meaning
) {}
