package com.ipnet.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipnet.assistant.config.OpenAiProperties;
import com.ipnet.assistant.model.OpenAiToolCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiAssistantClient {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OpenAiAssistantClient.class);

    private final OpenAiProperties properties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public OpenAiAssistantClient(
            OpenAiProperties properties,
            ObjectMapper objectMapper
    ) {
        this.properties = properties;
        this.objectMapper = objectMapper;

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(
                Math.max(1, properties.getConnectTimeoutSeconds()) * 1000
        );
        requestFactory.setReadTimeout(
                Math.max(1, properties.getReadTimeoutSeconds()) * 1000
        );

        this.restClient = RestClient.builder()
                .baseUrl(stripTrailingSlash(properties.getBaseUrl()))
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public boolean isConfigured() {
        return properties.isConfigured();
    }

    public int maxToolRounds() {
        return Math.max(1, properties.getMaxToolRounds());
    }

    public JsonNode createResponse(
            String instructions,
            List<Object> input,
            List<Map<String, Object>> tools
    ) {
        if (!isConfigured()) {
            throw new IllegalStateException(
                    "Le service IA n'est pas configuré."
            );
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", properties.getModel());
        body.put("instructions", instructions);
        body.put("input", input);
        body.put("tools", tools);
        body.put("tool_choice", "auto");
        body.put("parallel_tool_calls", false);
        body.put("store", false);
        body.put("max_output_tokens", Math.max(200, properties.getMaxOutputTokens()));
        body.put("text", Map.of("format", finalResponseFormat()));

        try {
            JsonNode response = restClient.post()
                    .uri("/v1/responses")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + properties.getApiKey().trim()
                    )
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                throw new IllegalStateException(
                        "Réponse vide reçue du service IA."
                );
            }

            return response;
        } catch (RestClientResponseException exception) {
            String responseBody = exception.getResponseBodyAsString();
            LOGGER.warn(
                    "Erreur OpenAI HTTP {} : {}",
                    exception.getStatusCode(),
                    responseBody
            );
            throw new IllegalStateException(
                    "Le service IA a refusé la requête (HTTP " +
                            exception.getStatusCode().value() + ").",
                    exception
            );
        }
    }

    public List<OpenAiToolCall> extractToolCalls(JsonNode response) {
        List<OpenAiToolCall> calls = new ArrayList<>();
        JsonNode output = response.path("output");

        if (!output.isArray()) {
            return calls;
        }

        for (JsonNode item : output) {
            if (!"function_call".equals(item.path("type").asText())) {
                continue;
            }

            String callId = item.path("call_id").asText();
            String name = item.path("name").asText();
            String rawArguments = item.path("arguments").asText("{}");

            Map<String, Object> arguments;
            try {
                arguments = objectMapper.readValue(
                        rawArguments,
                        new TypeReference<Map<String, Object>>() {
                        }
                );
            } catch (JsonProcessingException exception) {
                LOGGER.warn(
                        "Arguments invalides pour l'outil {} : {}",
                        name,
                        rawArguments
                );
                arguments = Map.of();
            }

            calls.add(new OpenAiToolCall(callId, name, arguments));
        }

        return calls;
    }

    public List<Object> extractOutputItems(JsonNode response) {
        List<Object> items = new ArrayList<>();
        JsonNode output = response.path("output");

        if (!output.isArray()) {
            return items;
        }

        for (JsonNode item : output) {
            items.add(objectMapper.convertValue(item, Object.class));
        }

        return items;
    }

    public String extractOutputText(JsonNode response) {
        JsonNode output = response.path("output");

        if (!output.isArray()) {
            return "";
        }

        StringBuilder text = new StringBuilder();

        for (JsonNode item : output) {
            if (!"message".equals(item.path("type").asText())) {
                continue;
            }

            JsonNode content = item.path("content");
            if (!content.isArray()) {
                continue;
            }

            for (JsonNode part : content) {
                if (!"output_text".equals(part.path("type").asText())) {
                    continue;
                }

                String value = part.path("text").asText();
                if (!value.isBlank()) {
                    if (!text.isEmpty()) {
                        text.append('\n');
                    }
                    text.append(value);
                }
            }
        }

        return text.toString().trim();
    }

    public String serializeToolOutput(Map<String, Object> value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            return "{\"ok\":false,\"message\":\"Résultat non sérialisable\"}";
        }
    }

    private Map<String, Object> finalResponseFormat() {
        Map<String, Object> format = new LinkedHashMap<>();
        format.put("type", "json_schema");
        format.put("name", "transia_assistant_response");
        format.put("strict", true);
        format.put("schema", finalResponseSchema());
        return format;
    }

    private Map<String, Object> finalResponseSchema() {
        Map<String, Object> propertiesMap = new LinkedHashMap<>();
        propertiesMap.put(
                "intent",
                Map.of(
                        "type", "string",
                        "enum", List.of(
                                "GREETING",
                                "HELP",
                                "BOOKING_HELP",
                                "SEARCH_TRIP",
                                "NEXT_TRIP",
                                "MY_RESERVATIONS",
                                "UNPAID_RESERVATIONS",
                                "PAYMENT_STATUS",
                                "TICKET",
                                "REFUND",
                                "TRACKING",
                                "MY_PARCELS",
                                "NEXT_PARCEL",
                                "PROFILE",
                                "THANKS",
                                "GOODBYE",
                                "GENERAL",
                                "UNKNOWN"
                        )
                )
        );
        propertiesMap.put("message", Map.of("type", "string"));
        propertiesMap.put(
                "suggestions",
                Map.of(
                        "type", "array",
                        "items", Map.of("type", "string"),
                        "maxItems", 4
                )
        );

        Map<String, Object> actionSchema = new LinkedHashMap<>();
        actionSchema.put("type", List.of("string", "null"));
        actionSchema.put(
                "enum",
                listWithNull(
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
                )
        );
        propertiesMap.put("actionId", actionSchema);

        Map<String, Object> dataProperties = new LinkedHashMap<>();
        dataProperties.put("id", nullableType("string"));
        dataProperties.put("reservationId", nullableType("string"));
        dataProperties.put("trajetId", nullableType("string"));
        dataProperties.put("colisId", nullableType("string"));
        dataProperties.put("route", nullableType("string"));
        dataProperties.put("date", nullableType("string"));
        dataProperties.put("count", nullableType("integer"));
        dataProperties.put("latitude", nullableType("number"));
        dataProperties.put("longitude", nullableType("number"));

        Map<String, Object> dataSchema = new LinkedHashMap<>();
        dataSchema.put("type", "object");
        dataSchema.put("properties", dataProperties);
        dataSchema.put("required", new ArrayList<>(dataProperties.keySet()));
        dataSchema.put("additionalProperties", false);
        propertiesMap.put("data", dataSchema);

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", propertiesMap);
        schema.put(
                "required",
                List.of("intent", "message", "suggestions", "actionId", "data")
        );
        schema.put("additionalProperties", false);
        return schema;
    }

    private Map<String, Object> nullableType(String type) {
        return Map.of("type", List.of(type, "null"));
    }

    private List<Object> listWithNull(String... values) {
        List<Object> result = new ArrayList<>();
        result.add(null);
        result.addAll(List.of(values));
        return result;
    }

    private String stripTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "https://api.openai.com";
        }

        String result = value.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
