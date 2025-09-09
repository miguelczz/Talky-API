package com.talky.backend.controller;

import com.talky.backend.model.User;
import com.talky.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // público, útil para probar conectividad
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    // privado, requiere JWT válido
    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getClaim("sub");
        String email = jwt.getClaim("email");
        String name = jwt.getClaim("username");

        // buscar usuario en la BD
        return userService.getByCognitoSub(sub)
                .orElseGet(() -> {
                    // si no existe, se crea en la BD
                    User newUser = User.builder()
                            .cognitoSub(sub)
                            .email(email != null ? email : sub + "@placeholder.com")
                            .name(name)
                            .role("student") // rol por defecto
                            .build();
                    return userService.save(newUser);
                });
    }

}