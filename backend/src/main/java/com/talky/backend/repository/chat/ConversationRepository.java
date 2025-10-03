package com.talky.backend.repository.chat;

import com.talky.backend.model.User;
import com.talky.backend.model.chat.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para manejar el acceso a datos de la entidad Conversation.
 * Extiende JpaRepository, lo que nos da automáticamente operaciones CRUD
 * (crear, leer, actualizar, eliminar) sobre las conversaciones.
 */
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    boolean existsByUserAndTitle(User user, String title);

    /**
     * Obtiene todas las conversaciones asociadas a un usuario específico.
     * Se usa cuando el estudiante quiere listar sus conversaciones en el frontend.
     */
    List<Conversation> findByUser(User user);

    /**
     * Cuenta cuántas conversaciones tiene actualmente un usuario, filtrando por su email.
     * <p>
     * Útil para verificar si el estudiante alcanzó el límite de conversaciones
     * antes de permitirle crear una nueva.
     */
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.user.email = :email")
    long countByEmail(@Param("email") String email);
}
