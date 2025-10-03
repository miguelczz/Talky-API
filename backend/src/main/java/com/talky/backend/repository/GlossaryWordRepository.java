package com.talky.backend.repository;

import com.talky.backend.model.GlossaryWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GlossaryWordRepository extends JpaRepository<GlossaryWord, UUID> {
    // Buscar todas las palabras de un usuario
    List<GlossaryWord> findByUser_Id(UUID userId);

    // Buscar si ya existe una palabra para ese usuario (para evitar duplicados)
    Optional<GlossaryWord> findByUser_IdAndWord(UUID userId, String word);

    // Buscar por id + user (evita inyección y verificar propiedad)
    Optional<GlossaryWord> findByIdAndUser_Id(UUID id, UUID userId);
}