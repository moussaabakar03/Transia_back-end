package com.ipnet.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ipnet.enums.StatutCollecte;

public class DemandeCollecteDto {
    private UUID id;
    private String adresseCollecte;
    private Double latitude;
    private Double longitude;
    private LocalDateTime dateHeureCollecte;
    private StatutCollecte statut;
    private UUID expediteurId;
    private String expediteurNom;
    private UUID agenceId;
    private String agenceNom;
    private UUID livreurId;
    private String livreurNom;
    private UUID colisId;
    private String colisNumeroSuivi;
    private UUID tourneeId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getAdresseCollecte() { return adresseCollecte; }
    public void setAdresseCollecte(String adresseCollecte) { this.adresseCollecte = adresseCollecte; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getDateHeureCollecte() { return dateHeureCollecte; }
    public void setDateHeureCollecte(LocalDateTime dateHeureCollecte) { this.dateHeureCollecte = dateHeureCollecte; }

    public StatutCollecte getStatut() { return statut; }
    public void setStatut(StatutCollecte statut) { this.statut = statut; }

    public UUID getExpediteurId() { return expediteurId; }
    public void setExpediteurId(UUID expediteurId) { this.expediteurId = expediteurId; }

    public String getExpediteurNom() { return expediteurNom; }
    public void setExpediteurNom(String expediteurNom) { this.expediteurNom = expediteurNom; }

    public UUID getAgenceId() { return agenceId; }
    public void setAgenceId(UUID agenceId) { this.agenceId = agenceId; }

    public String getAgenceNom() { return agenceNom; }
    public void setAgenceNom(String agenceNom) { this.agenceNom = agenceNom; }

    public UUID getLivreurId() { return livreurId; }
    public void setLivreurId(UUID livreurId) { this.livreurId = livreurId; }

    public String getLivreurNom() { return livreurNom; }
    public void setLivreurNom(String livreurNom) { this.livreurNom = livreurNom; }

    public UUID getColisId() { return colisId; }
    public void setColisId(UUID colisId) { this.colisId = colisId; }

    public String getColisNumeroSuivi() { return colisNumeroSuivi; }
    public void setColisNumeroSuivi(String colisNumeroSuivi) { this.colisNumeroSuivi = colisNumeroSuivi; }

    public UUID getTourneeId() { return tourneeId; }
    public void setTourneeId(UUID tourneeId) { this.tourneeId = tourneeId; }
}
