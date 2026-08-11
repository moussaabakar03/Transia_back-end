package com.ipnet.dto;

import java.util.UUID;

public class ChauffeurProblemeDto {

    private UUID id;
    private UUID trajetId;

    // Il s'agit du publicId UUID du chauffeur,
    // et non de son identifiant numérique interne.
    private UUID chauffeurId;

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

    public UUID getChauffeurId() {
        return chauffeurId;
    }

    public void setChauffeurId(UUID chauffeurId) {
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