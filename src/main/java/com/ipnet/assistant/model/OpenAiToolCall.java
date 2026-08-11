package com.ipnet.assistant.model;

import java.util.Map;

/**
 * Représente un appel d'outil demandé par le modèle.
 *
 * Le nom du fichier doit être exactement :
 * OpenAiToolCall.java
 */
public record OpenAiToolCall(
        String callId,
        String name,
        Map<String, Object> arguments
) {
    public OpenAiToolCall {
        arguments = arguments == null ? Map.of() : Map.copyOf(arguments);
    }
}
