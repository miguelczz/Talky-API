package com.talky.backend.service;

import com.talky.backend.model.GlossaryWord;
import com.talky.backend.model.User;
import com.talky.backend.repository.GlossaryWordRepository;
import com.talky.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GlossaryWordService {

    private final GlossaryWordRepository glossaryRepo;
    private final UserRepository userRepository;

    public GlossaryWordService(GlossaryWordRepository glossaryRepo, UserRepository userRepository) {
        this.glossaryRepo = glossaryRepo;
        this.userRepository = userRepository;
    }

    public GlossaryWord saveForUser(UUID userId, String word, String meaning) {
        // Validar existencia usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Evitar duplicados por usuario
        if (glossaryRepo.findByUser_IdAndWord(userId, word).isPresent()) {
            throw new IllegalStateException("La palabra ya existe en el glosario del usuario");
        }

        GlossaryWord gw = GlossaryWord.builder()
                .user(user)
                .word(word)
                .meaning(meaning)
                .build();

        return glossaryRepo.save(gw);
    }

    public List<GlossaryWord> findByUser(UUID userId) {
        return glossaryRepo.findByUser_Id(userId);
    }

    public GlossaryWord toggleArchive(UUID userId, UUID wordId) {
        GlossaryWord word = glossaryRepo.findByIdAndUser_Id(wordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Palabra no encontrada"));

        word.setArchived(!word.isArchived());
        return glossaryRepo.save(word);
    }

    // Actualizar palabra y significado
    public GlossaryWord updateWord(UUID userId, UUID wordId, String newWord, String newMeaning) {
        GlossaryWord word = glossaryRepo.findByIdAndUser_Id(wordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Palabra no encontrada"));

        if (newWord != null && !newWord.trim().isEmpty()) {
            String normalized = newWord.trim();
            // comprobar duplicado (si cambió)
            if (!word.getWord().equalsIgnoreCase(normalized)
                    && glossaryRepo.findByUser_IdAndWord(userId, normalized).isPresent()) {
                throw new IllegalStateException("La palabra ya existe en el glosario del usuario");
            }
            word.setWord(normalized);
        }

        if (newMeaning != null && !newMeaning.trim().isEmpty()) {
            word.setMeaning(newMeaning.trim());
        }

        return glossaryRepo.save(word);
    }

    // Eliminar
    public void deleteWord(UUID userId, UUID wordId) {
        GlossaryWord word = glossaryRepo.findByIdAndUser_Id(wordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Palabra no encontrada"));
        glossaryRepo.delete(word);
    }
}
