package com.talky.backend.service.chat;

import com.talky.backend.dto.summary.SummaryRequestDto;
import com.talky.backend.dto.summary.SummaryResponseDto;
import com.talky.backend.model.chat.*;
import com.talky.backend.repository.chat.ConversationSummaryRepository;
import com.talky.backend.repository.chat.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationSummaryService {

    private final ConversationSummaryRepository summaryRepository;
    private final MessageRepository messageRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // Palabras irrelevantes que podemos ignorar para ahorrar tokens
    private static final Set<String> IRRELEVANT_MESSAGES = Set.of(
            "ok", "okay", "vale", "listo", "gracias", "sí", "si", "no", "ajá", "aja"
    );

    /**
     * Genera un resumen real de los mensajes usando N8N.
     * NOTA: excluye mensajes de tipo SUMMARY para evitar que los resúmenes se auto-acumulen.
     */
    public String generateSummary(List<Message> messages) {
        try {
            // 1) Excluir mensajes que sean ya de tipo SUMMARY (no queremos resumir resúmenes)
            // 2) Filtrar mensajes irrelevantes (muy cortos, "gracias", etc.) para ahorrar tokens
            List<Message> filteredMessages = messages.stream()
                    .filter(m -> m.getType() != MessageType.SUMMARY) // <-- clave: excluir resúmenes previos
                    .filter(m -> {
                        String content = Optional.ofNullable(m.getContent()).orElse("").trim().toLowerCase();
                        if (content.isEmpty()) return false;
                        // Ignorar mensajes extremadamente cortos o irrelevantes
                        if (content.length() <= 2) return false;
                        if (IRRELEVANT_MESSAGES.contains(content)) return false;
                        // Ignorar respuestas de IA demasiado cortas que no aportan contexto
                        if (m.getType() == MessageType.AI && content.length() < 5) return false;
                        return true;
                    })
                    .collect(Collectors.toList());

            if (filteredMessages.isEmpty()) {
                return "Sin información relevante para resumir.";
            }

            // Concatenar historial optimizado en un solo texto
            String history = filteredMessages.stream()
                    .map(m -> "[" + m.getType() + "] " + m.getContent())
                    .collect(Collectors.joining(" | "));

            // Armar payload para N8N
            SummaryRequestDto request = new SummaryRequestDto(history);

            // URL de tu workflow N8N (ajústala según tu entorno)
            String url = "http://localhost:5678/webhook/talky-summary";

            SummaryResponseDto response = restTemplate.postForObject(
                    url,
                    request,
                    SummaryResponseDto.class
            );

            return response != null ? response.getSummary() : "No se recibió resumen de la IA";

        } catch (Exception e) {
            // puedes loguear e.getMessage() para diagnóstico
            return "Error al generar resumen con la IA";
        }
    }

    /**
     * Guarda el último resumen en la tabla de ConversationSummary.
     * (Se elimina cualquier resumen anterior de esa conversación para ahorrar espacio)
     */
    @Transactional
    public ConversationSummary saveSummary(Conversation conversation, String summaryText) {
        // 1) Borrar resúmenes anteriores (tabla)
        summaryRepository.deleteByConversation(conversation);

        // 2) Borrar mensajes previos de tipo SUMMARY (evitar acumulación en message table)
        List<Message> prevSummaries = messageRepository.findByConversationAndType(conversation, MessageType.SUMMARY);
        if (!prevSummaries.isEmpty()) {
            messageRepository.deleteAll(prevSummaries);
        }

        // 3) Guardar nuevo resumen en la tabla de resúmenes
        ConversationSummary summary = ConversationSummary.builder()
                .conversation(conversation)
                .summary(summaryText)
                .build();
        return summaryRepository.save(summary);
    }

    /**
     * Guarda el resumen como un mensaje dentro del historial.
     * (Se asume que saveSummary(...) ya eliminó cualquier summary previo)
     */
    @Transactional
    public Message saveSummaryAsMessage(Conversation conversation, String summaryText) {
        Message summaryMessage = Message.builder()
                .conversation(conversation)
                .type(MessageType.SUMMARY)
                .content(summaryText)
                .build();
        return messageRepository.save(summaryMessage);
    }

    /**
     * Obtiene el último resumen de una conversación.
     */
    public ConversationSummary getLatestSummary(Conversation conversation) {
        return summaryRepository.findTopByConversationOrderByCreatedAtDesc(conversation)
                .orElse(null);
    }
}