package com.talky.backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar el perfil de un usuario.
 * Todos los campos son opcionales, pero al menos uno debe ser proporcionado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    private String phoneNumber;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$|^$", 
             message = "La fecha debe estar en formato YYYY-MM-DD o estar vacía")
    private String birthdate;

    @Pattern(regexp = "^[MFO]$|^$|^null$", 
             message = "El género debe ser 'M', 'F', 'O' o estar vacío")
    private String gender;

    /**
     * Valida que al menos un campo esté presente y no vacío.
     */
    public boolean hasAtLeastOneField() {
        return (name != null && !name.trim().isEmpty()) ||
               (phoneNumber != null && !phoneNumber.trim().isEmpty()) ||
               (birthdate != null && !birthdate.trim().isEmpty()) ||
               (gender != null && !gender.trim().isEmpty() && !gender.trim().equalsIgnoreCase("null"));
    }

    /**
     * Normaliza el género: convierte a null si está vacío o es "null".
     */
    public String getNormalizedGender() {
        if (gender == null || gender.trim().isEmpty() || gender.trim().equalsIgnoreCase("null")) {
            return null;
        }
        return gender.trim().toUpperCase();
    }
}

