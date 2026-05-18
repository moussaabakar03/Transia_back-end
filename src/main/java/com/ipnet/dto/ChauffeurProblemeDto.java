package com.ipnet.dto;

import java.util.UUID;

public class ChauffeurProblemeDto {

    private UUID id;
    private UUID trajetId;
    private Long chauffeurId;
    private String typeProbleme;
    private String description;
    private String statut;
    private String trajetLabel;
    private String chauffeurNom;
    private String dateCreation;

    public ChauffeurProblemeDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTrajetId() {
        return trajetId;
    }

    public void setTrajetId(UUID trajetId) {
        this.trajetId = trajetId;
    }

    public Long getChauffeurId() {
        return chauffeurId;
    }

    public void setChauffeurId(Long chauffeurId) {
        this.chauffeurId = chauffeurId;
    }

    public String getTypeProbleme() {
        return typeProbleme;
    }

    public void setTypeProbleme(String typeProbleme) {
        this.typeProbleme = typeProbleme;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getTrajetLabel() {
        return trajetLabel;
    }

    public void setTrajetLabel(String trajetLabel) {
        this.trajetLabel = trajetLabel;
    }

    public String getChauffeurNom() {
        return chauffeurNom;
    }

    public void setChauffeurNom(String chauffeurNom) {
        this.chauffeurNom = chauffeurNom;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }
}