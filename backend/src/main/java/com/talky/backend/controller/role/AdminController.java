package com.talky.backend.controller.role;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/profile")
    public ResponseEntity<String> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("email");
        return ResponseEntity.ok("Perfil del administrador: " + email);
    }
}
