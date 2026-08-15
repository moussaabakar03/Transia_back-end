package com.ipnet.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipnet.assistant.dto.AssistantRequest;
import com.ipnet.assistant.dto.AssistantResponse;
import com.ipnet.assistant.model.OpenAiToolCall;
import com.ipnet.security.UserDetailsImpl;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AssistantServiceImplement implements AssistantService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AssistantServiceImplement.class);

    private static final Set<String> ALLOWED_ACTIONS = Set.of(
            "OPEN_TRIPS",
            "OPEN_RESERVATIONS",
            "OPEN_RESERVATION",
            "OPEN_TICKET",
            "OPEN_REFUND",
            "OPEN_TRACKING",
            "OPEN_PARCELS",
            "OPEN_PARCEL",
            "OPEN_PROFILE",
            "OPEN_AGENCIES",
            "OPEN_CHAUFFEUR_TRIP"
    );

    private final OpenAiAssistantClient openAiClient;
    private final AssistantToolDefinitions toolDefinitions;
    private final AssistantToolExecutor toolExecutor;
    private final AssistantPromptFactory promptFactory;
    private final AssistantConversationMemory conversationMemory;
    private final AssistantFallbackService fallbackService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public AssistantServiceImplement(
            OpenAiAssistantClient openAiClient,
            AssistantToolDefinitions toolDefinitions,
            AssistantToolExecutor toolExecutor,
            AssistantPromptFactory promptFactory,
            AssistantConversationMemory conversationMemory,
            AssistantFallbackService fallbackService,
            UserRepository userRepository,
            ObjectMapper objectMapper
    ) {
        this.openAiClient = openAiClient;
        this.toolDefinitions = toolDefinitions;
        this.toolExecutor = toolExecutor;
        this.promptFactory = promptFactory;
        this.conversationMemory = conversationMemory;
        this.fallbackService = fallbackService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public AssistantResponse answer(
            AssistantRequest request,
            Authentication authentication,
            String authorizationHeader
    ) {
        String message = request == null || request.getMessage() == null
                ? ""
                : request.getMessage().trim();

        if (message.isEmpty()) {
            return AssistantResponse.simple(
                    "UNKNOWN",
                    "Écrivez une question pour commencer.",
                    List.of(
                            "Quel est mon prochain trajet ?",
                            "Voir mes réservations"
                    )
            );
        }

        if (!openAiClient.isConfigured()) {
            return fallbackService.answer(
                    request,
                    authentication,
                    authorizationHeader
            );
        }

        try {
            User user = currentUser(authentication);
            String conversationKey = conversationKey(user, authentication);

            List<Object> input = conversationMemory.buildInput(conversationKey);
            input.add(messageInput(message));

            String instructions = promptFactory.build(
                    user,
                    authentication,
                    request == null ? Map.of() : request.getContext()
            );

            AssistantResponse response = runAgentLoop(
                    instructions,
                    input,
                    authentication
            );

            conversationMemory.remember(
                    conversationKey,
                    message,
                    response.getMessage()
            );

            return response;
        } catch (RuntimeException exception) {
            LOGGER.warn(
                    "Le mode IA de TransIA a échoué. Utilisation du moteur local : {}",
                    exception.getMessage()
            );

            return fallbackService.answer(
                    request,
                    authentication,
                    authorizationHeader
            );
        }
    }

    private AssistantResponse runAgentLoop(
            String instructions,
            List<Object> input,
            Authentication authentication
    ) {
        int maximumRounds = openAiClient.maxToolRounds();

        for (int round = 0; round < maximumRounds; round++) {
            JsonNode response = openAiClient.createResponse(
                    instructions,
                    input,
                    toolDefinitions.all()
            );

            List<OpenAiToolCall> toolCalls =
                    openAiClient.extractToolCalls(response);

            if (toolCalls.isEmpty()) {
                return parseFinalResponse(
                        openAiClient.extractOutputText(response)
                );
            }

            // Les éléments de sortie, dont les function_call, sont renvoyés
            // au modèle avant les function_call_output correspondants.
            input.addAll(openAiClient.extractOutputItems(response));

            for (OpenAiToolCall toolCall : toolCalls) {
                Map<String, Object> result = toolExecutor.execute(
                        toolCall.name(),
                        toolCall.arguments(),
                        authentication
                );

                input.add(toolOutput(
                        toolCall.callId(),
                        openAiClient.serializeToolOutput(result)
                ));
            }
        }

        throw new IllegalStateException(
                "Le nombre maximal d'appels d'outils a été atteint."
        );
    }

    private AssistantResponse parseFinalResponse(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            throw new IllegalStateException(
                    "Le service IA n'a retourné aucun texte."
            );
        }

        try {
            AssistantResponse response = objectMapper.readValue(
                    rawText,
                    AssistantResponse.class
            );

            return sanitize(response);
        } catch (JsonProcessingException exception) {
            LOGGER.warn(
                    "Réponse structurée IA invalide : {}",
                    rawText
            );
            throw new IllegalStateException(
                    "La réponse du service IA est invalide.",
                    exception
            );
        }
    }

    private AssistantResponse sanitize(AssistantResponse response) {
        if (response == null ||
                response.getMessage() == null ||
                response.getMessage().isBlank()) {
            throw new IllegalStateException(
                    "Le message final du service IA est vide."
            );
        }

        String intent = response.getIntent() == null ||
                response.getIntent().isBlank()
                ? "GENERAL"
                : response.getIntent().trim().toUpperCase();

        String actionId = response.getActionId();
        if (actionId != null) {
            actionId = actionId.trim().toUpperCase();
            if (!ALLOWED_ACTIONS.contains(actionId)) {
                actionId = null;
            }
        }

        LinkedHashSet<String> uniqueSuggestions = new LinkedHashSet<>();
        if (response.getSuggestions() != null) {
            for (String suggestion : response.getSuggestions()) {
                if (suggestion == null) {
                    continue;
                }

                String clean = suggestion.trim();
                if (!clean.isEmpty()) {
                    uniqueSuggestions.add(clean);
                }

                if (uniqueSuggestions.size() == 4) {
                    break;
                }
            }
        }

        Map<String, Object> cleanData = new LinkedHashMap<>();
        if (response.getData() != null) {
            response.getData().forEach((key, value) -> {
                if (key != null && value != null) {
                    cleanData.put(key, value);
                }
            });
        }

        return new AssistantResponse(
                intent,
                response.getMessage().trim(),
                new ArrayList<>(uniqueSuggestions),
                actionId,
                cleanData
        );
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "Utilisateur non authentifié."
            );
        }

        if (authentication.getPrincipal() instanceof UserDetailsImpl details) {
            return userRepository.findByPublicId(details.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Utilisateur connecté introuvable."
                    ));
        }

        return userRepository.findByTelephone(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Utilisateur connecté introuvable."
                ));
    }

    private String conversationKey(
            User user,
            Authentication authentication
    ) {
        if (user != null && user.getPublicId() != null) {
            return user.getPublicId().toString();
        }

        if (authentication != null && authentication.getName() != null) {
            return authentication.getName();
        }

        return "anonymous";
    }

    private Map<String, Object> messageInput(String message) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("role", "user");
        item.put("content", message);
        return item;
    }

    private Map<String, Object> toolOutput(
            String callId,
            String output
    ) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("type", "function_call_output");
        item.put("call_id", callId);
        item.put("output", output);
        return item;
    }
}
