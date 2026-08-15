package com.ipnet.assistant.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssistantResponse {

    private String intent;
    private String message;
    private List<String> suggestions = new ArrayList<>();
    private String actionId;
    private Map<String, Object> data = new HashMap<>();

    public AssistantResponse() {
    }

    public AssistantResponse(
            String intent,
            String message,
            List<String> suggestions,
            String actionId,
            Map<String, Object> data
    ) {
        this.intent = intent;
        this.message = message;
        this.suggestions = suggestions == null ? new ArrayList<>() : suggestions;
        this.actionId = actionId;
        this.data = data == null ? new HashMap<>() : data;
    }

    public static AssistantResponse simple(
            String intent,
            String message,
            List<String> suggestions
    ) {
        return new AssistantResponse(
                intent,
                message,
                suggestions,
                null,
                new HashMap<>()
        );
    }

    public static AssistantResponse action(
            String intent,
            String message,
            List<String> suggestions,
            String actionId,
            Map<String, Object> data
    ) {
        return new AssistantResponse(
                intent,
                message,
                suggestions,
                actionId,
                data
        );
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions == null ? new ArrayList<>() : suggestions;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data == null ? new HashMap<>() : data;
    }
}