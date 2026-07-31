package com.ipnet.assistant.controller;

import com.ipnet.assistant.dto.AssistantRequest;
import com.ipnet.assistant.dto.AssistantResponse;
import com.ipnet.assistant.service.AssistantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(
            AssistantService assistantService
    ) {
        this.assistantService = assistantService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AssistantResponse> chat(
            @Valid @RequestBody AssistantRequest request,
            Authentication authentication,
            @RequestHeader(
                    value = HttpHeaders.AUTHORIZATION,
                    required = false
            )
            String authorizationHeader
    ) {
        AssistantResponse response = assistantService.answer(
                request,
                authentication,
                authorizationHeader
        );

        return ResponseEntity.ok(response);
    }
}