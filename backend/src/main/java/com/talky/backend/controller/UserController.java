package com.talky.backend.controller;

import com.talky.backend.model.User;
import com.talky.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Devuelve todos los usuarios de la BD.
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
