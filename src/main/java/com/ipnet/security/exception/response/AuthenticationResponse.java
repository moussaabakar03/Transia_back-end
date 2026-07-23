package com.ipnet.security.exception.response;

import java.util.List;
import java.util.UUID;

public class AuthenticationResponse {

    // Identifiant public UUID
    private UUID id;

    // Identifiant numérique de la base de données
    private Long numericId;

    private String fullName;
    private String username;
    private List<String> roles;
    private String token;
    private String type = "Bearer";

    public AuthenticationResponse(
            String accessToken,
            UUID id,
            Long numericId,
            String fullName,
            String username,
            List<String> roles
    ) {
        this.token = accessToken;
        this.id = id;
        this.numericId = numericId;
        this.fullName = fullName;
        this.username = username;
        this.roles = roles;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getNumericId() {
        return numericId;
    }

    public void setNumericId(Long numericId) {
        this.numericId = numericId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}