package com.talky.backend.controller;

import com.talky.backend.dto.glossary.GlossaryRequestDto;
import com.talky.backend.dto.glossary.GlossaryResponseDto;
import com.talky.backend.model.GlossaryWord;
import com.talky.backend.model.User;
import com.talky.backend.repository.UserRepository;
import com.talky.backend.service.GlossaryWordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Endpoints para el glosario.
 */
@RestController
@RequestMapping("/api/glossary")
public class GlossaryWordController {

    private final GlossaryWordService glossaryService;
    private final UserRepository userRepository;

    public GlossaryWordController(GlossaryWordService glossaryService, UserRepository userRepository) {
        this.glossaryService = glossaryService;
        this.userRepository = userRepository;
    }

    // ========================
    // Métodos públicos (endpoints)
    // ========================

    /**
     * Obtiene todas las palabras del usuario autenticado
     */
    @GetMapping
    public ResponseEntity<List<GlossaryResponseDto>> getUserWords(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        List<GlossaryWord> words = glossaryService.findByUser(user.getId());
        var resp = words.stream()
                .map(GlossaryResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    /**
     * Crea una palabra nueva
     */
    @PostMapping
    public ResponseEntity<GlossaryResponseDto> addWord(@RequestBody GlossaryRequestDto req,
                                                       Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        GlossaryWord saved = glossaryService.saveForUser(user.getId(), req.word(), req.meaning());
        return ResponseEntity.status(HttpStatus.CREATED).body(GlossaryResponseDto.fromEntity(saved));
    }

    /**
     * Archivar / Desarchivar una palabra
     */
    @PatchMapping("/{id}/archive")
    public ResponseEntity<GlossaryResponseDto> toggleArchive(
            @PathVariable UUID id,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);
        GlossaryWord updated = glossaryService.toggleArchive(user.getId(), id);
        return ResponseEntity.ok(GlossaryResponseDto.fromEntity(updated));
    }

    /**
     * Actualizar palabra y significado
     */
    @PutMapping("/{id}")
    public ResponseEntity<GlossaryResponseDto> updateWord(
            @PathVariable UUID id,
            @RequestBody GlossaryRequestDto req,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);
        GlossaryWord updated = glossaryService.updateWord(user.getId(), id, req.word(), req.meaning());
        return ResponseEntity.ok(GlossaryResponseDto.fromEntity(updated));
    }

    /**
     * Elimina una palabra de forma definitiva
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable UUID id, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        glossaryService.deleteWord(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    // ========================
    // Métodos privados auxiliares
    // ========================

    /**
     * Obtiene el usuario autenticado a partir del JWT
     */
    private User getAuthenticatedUser(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticación inválida");
        }

        Jwt jwt = jwtAuth.getToken();
        String cognitoSub = jwt.getClaimAsString("sub");

        return userRepository.findByCognitoSub(cognitoSub)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no registrado"));
    }
}