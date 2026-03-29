package com.ipnet.dto;


import java.util.List;

public class ReservationRequestDto {
    private Long userId;          
    private Long trajetId;       
    private int nombrePlace;     
    private String nomResponsable; 
    private List<String> nomsPassagers; 

    public ReservationRequestDto() {}

    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTrajetId() { return trajetId; }
    public void setTrajetId(Long trajetId) { this.trajetId = trajetId; }

    public int getNombrePlace() { return nombrePlace; }
    public void setNombrePlace(int nombrePlace) { this.nombrePlace = nombrePlace; }

    public String getNomResponsable() { return nomResponsable; }
    public void setNomResponsable(String nomResponsable) { this.nomResponsable = nomResponsable; }

    public List<String> getNomsPassagers() { return nomsPassagers; }
    public void setNomsPassagers(List<String> nomsPassagers) { this.nomsPassagers = nomsPassagers; }
}