package com.talky.backend.controller;

import com.talky.backend.dto.UserSyncRequest;
import com.talky.backend.model.User;
import com.talky.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // Endpoint público para verificar conectividad
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    // Endpoint privado: requiere un JWT válido (ID Token)
    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getClaim("sub");
        String email = jwt.getClaim("email");
        String name = jwt.getClaim("name");
        String birthdate = jwt.getClaim("birthdate");
        String gender = jwt.getClaim("gender");
        String phoneNumber = jwt.getClaim("phone_number");

        return userService.getByCognitoSub(sub)
                .orElseGet(() ->
                        userService.getByEmail(email)
                                .orElseGet(() -> {
                                    User newUser = User.builder()
                                            .cognitoSub(sub)
                                            .email(email)
                                            .name(name)
                                            .birthdate(birthdate)
                                            .gender(gender)
                                            .phoneNumber(phoneNumber)
                                            .role(User.Role.STUDENT)
                                            .build();
                                    return userService.save(newUser);
                                })
                );
    }

    // Endpoint privado: sincroniza datos del usuario autenticado con la BD
    @PostMapping("/sync")
    public ResponseEntity<User> syncUser(@AuthenticationPrincipal Jwt jwt) {
        UserSyncRequest req = new UserSyncRequest();
        req.setSub(jwt.getClaim("sub"));
        req.setEmail(jwt.getClaim("email"));
        req.setName(jwt.getClaim("name"));
        req.setBirthdate(jwt.getClaim("birthdate"));
        req.setGender(jwt.getClaim("gender"));
        req.setPhoneNumber(jwt.getClaim("phone_number"));
        req.setRole(User.Role.STUDENT);

        User user = userService.syncUser(req);
        return ResponseEntity.ok(user);
    }
}
