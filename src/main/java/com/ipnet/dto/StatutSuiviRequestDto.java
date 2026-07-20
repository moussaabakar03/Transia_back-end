package com.ipnet.dto;

public class StatutSuiviRequestDto {

    private String statut;
    private String message;

    public StatutSuiviRequestDto() {
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}