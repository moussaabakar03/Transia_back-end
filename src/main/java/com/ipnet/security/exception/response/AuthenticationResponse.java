package com.ipnet.security.exception.response;

import java.util.List;
import java.util.UUID;

public class AuthenticationResponse {
    private UUID id;
    private String fullName;
    private String telephone;
    private List<String> roles;
    private String token;
    private String type = "Bearer";
    private UUID agenceId;
    private String agenceNom;
    private UUID villeId;
    private String villeNom;


    public AuthenticationResponse(String accessToken, UUID id, String fullName, String telephone,
                                   List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.fullName = fullName;
        this.telephone = telephone;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public List<String> getRoles() {
        return roles;
    }

    public UUID getAgenceId() { return agenceId; }
    public void setAgenceId(UUID agenceId) { this.agenceId = agenceId; }

    public String getAgenceNom() { return agenceNom; }
    public void setAgenceNom(String agenceNom) { this.agenceNom = agenceNom; }

    public UUID getVilleId() { return villeId; }
    public void setVilleId(UUID villeId) { this.villeId = villeId; }

    public String getVilleNom() { return villeNom; }
    public void setVilleNom(String villeNom) { this.villeNom = villeNom; }
}
