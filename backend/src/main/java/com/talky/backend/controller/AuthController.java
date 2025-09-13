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

    // endpoint publico para verificar conectividad
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    // endpoint privado, requiere un JWT valido (ID Token)
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
                                            .role("student")
                                            .build();
                                    return userService.save(newUser);
                                })
                );
    }

}