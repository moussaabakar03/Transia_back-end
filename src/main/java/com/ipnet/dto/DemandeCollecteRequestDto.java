package com.ipnet.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DemandeCollecteRequestDto {
    private String adresseCollecte;
    private Double latitude;
    private Double longitude;
    private LocalDateTime dateHeureCollecte;
    private UUID agenceId;

    public String getAdresseCollecte() { return adresseCollecte; }
    public void setAdresseCollecte(String adresseCollecte) { this.adresseCollecte = adresseCollecte; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getDateHeureCollecte() { return dateHeureCollecte; }
    public void setDateHeureCollecte(LocalDateTime dateHeureCollecte) { this.dateHeureCollecte = dateHeureCollecte; }

    public UUID getAgenceId() { return agenceId; }
    public void setAgenceId(UUID agenceId) { this.agenceId = agenceId; }
}
