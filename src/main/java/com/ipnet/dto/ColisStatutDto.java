package com.ipnet.dto;

import java.time.LocalDateTime;

import com.ipnet.enums.StatutColis;

// Vue publique et minimale exposée par /api/colis/suivi/{numeroSuivi} : pas de coordonnées
// personnelles ni de détails de tarification, juste de quoi suivre le colis avec son numéro.
public class ColisStatutDto {
    private String numeroSuivi;
    private StatutColis statut;
    private String description;
    private String agenceDepartNom;
    private String agenceArriveeNom;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLivraison;

    public String getNumeroSuivi() { return numeroSuivi; }
    public void setNumeroSuivi(String numeroSuivi) { this.numeroSuivi = numeroSuivi; }

    public StatutColis getStatut() { return statut; }
    public void setStatut(StatutColis statut) { this.statut = statut; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAgenceDepartNom() { return agenceDepartNom; }
    public void setAgenceDepartNom(String agenceDepartNom) { this.agenceDepartNom = agenceDepartNom; }

    public String getAgenceArriveeNom() { return agenceArriveeNom; }
    public void setAgenceArriveeNom(String agenceArriveeNom) { this.agenceArriveeNom = agenceArriveeNom; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }
}
