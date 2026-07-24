package com.ipnet.dto;


import java.util.List;
import java.util.UUID;

public class ReservationRequestDto {
    private UUID trajetId;
    private int nombrePlace;
    private String nomResponsable;
    private List<String> nomsPassagers;

    private List<String> siegesChoisis;  // facultatif, peut être vide ou null

    public ReservationRequestDto() {}

    public UUID getTrajetId() { return trajetId; }
    public void setTrajetId(UUID trajetId) { this.trajetId = trajetId; }

    public int getNombrePlace() { return nombrePlace; }
    public void setNombrePlace(int nombrePlace) { this.nombrePlace = nombrePlace; }

    public String getNomResponsable() { return nomResponsable; }
    public void setNomResponsable(String nomResponsable) { this.nomResponsable = nomResponsable; }

    public List<String> getNomsPassagers() { return nomsPassagers; }
    public void setNomsPassagers(List<String> nomsPassagers) { this.nomsPassagers = nomsPassagers; }

    public List<String> getSiegesChoisis() { return siegesChoisis; }
    
    public void setSiegesChoisis(List<String> siegesChoisis) { this.siegesChoisis = siegesChoisis; }
}