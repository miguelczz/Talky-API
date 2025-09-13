package com.talky.backend.controller;

import com.talky.backend.dto.UserSyncRequest;
import com.talky.backend.model.User;
import com.talky.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint para reflejar al usuario en la base de datos local
    @PostMapping("/sync")
    public ResponseEntity<User> syncUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UserSyncRequest req) {

        String cognitoSub = jwt.getSubject(); // se obtiene el sub desde el token JWT
        User user = userService.syncUser(cognitoSub, req);
        return ResponseEntity.ok(user);
    }
}