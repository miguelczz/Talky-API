package com.talky.backend.dto;

import com.talky.backend.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar el rol de un usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleRequest {
    @NotNull(message = "El rol es requerido")
    private User.Role role;
}

