package com.ipnet.assistant.service;

import com.ipnet.assistant.dto.AssistantRequest;
import com.ipnet.assistant.dto.AssistantResponse;
import org.springframework.security.core.Authentication;

public interface AssistantService {

    AssistantResponse answer(
            AssistantRequest request,
            Authentication authentication,
            String authorizationHeader
    );
}