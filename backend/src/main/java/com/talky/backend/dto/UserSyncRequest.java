package com.talky.backend.dto;

import com.talky.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Este DTO representa los datos que llegan desde el frontend
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSyncRequest {
    private String sub; // Identificador único de Cognito
    private String email;
    private String name;
    private User.Role role; // ahora es enum, no String
    private String phoneNumber;
    private String birthdate;
    private String gender;
}
