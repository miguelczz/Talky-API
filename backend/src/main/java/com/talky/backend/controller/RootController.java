package com.talky.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    // Ruta raíz de la versión 1 de la API.
    @GetMapping("/api/v1")
    public String apiRoot() {
        return "Bienvenido a Talky API";
    }

    // Ruta de estado de la API.
    @GetMapping("/health")
    public String health() {
        return "API corriendo correctamente ✅";
    }
}
