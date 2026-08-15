package com.ipnet.assistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashMap;
import java.util.Map;

public class AssistantRequest {

    @NotBlank(message = "Le message est obligatoire.")
    @Size(max = 1000, message = "Le message ne doit pas dépasser 1000 caractères.")
    private String message;

    private Map<String, Object> context = new HashMap<>();

    public AssistantRequest() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context == null ? new HashMap<>() : context;
    }
}
