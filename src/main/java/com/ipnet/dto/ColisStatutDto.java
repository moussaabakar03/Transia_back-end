package com.ipnet.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ipnet.enums.ModeRemise;
import com.ipnet.enums.StatutColis;

// Vue publique et minimale exposée par /api/colis/suivi/{numeroSuivi} : pas de coordonnées
// personnelles ni de détails de tarification, juste de quoi suivre le colis avec son numéro.
public class ColisStatutDto {
    private UUID id;
    private String numeroSuivi;
    private StatutColis statut;
    private String description;
    private String agenceDepartNom;
    private String agenceArriveeNom;
    private ModeRemise modeRemise;
    private String codeRetrait;
    private String qrCode;
    private String lienSuivi;
    private UUID trajetId;
    private String trajetInfo;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLivraison;
    private List<HistoriqueColisDto> historique;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTrajetId() { return trajetId; }
    public void setTrajetId(UUID trajetId) { this.trajetId = trajetId; }

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

    public ModeRemise getModeRemise() { return modeRemise; }
    public void setModeRemise(ModeRemise modeRemise) { this.modeRemise = modeRemise; }

    public String getCodeRetrait() { return codeRetrait; }
    public void setCodeRetrait(String codeRetrait) { this.codeRetrait = codeRetrait; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public String getLienSuivi() { return lienSuivi; }
    public void setLienSuivi(String lienSuivi) { this.lienSuivi = lienSuivi; }

    public String getTrajetInfo() { return trajetInfo; }
    public void setTrajetInfo(String trajetInfo) { this.trajetInfo = trajetInfo; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }

    public List<HistoriqueColisDto> getHistorique() { return historique; }
    public void setHistorique(List<HistoriqueColisDto> historique) { this.historique = historique; }
}
