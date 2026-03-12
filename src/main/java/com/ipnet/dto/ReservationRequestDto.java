package com.ipnet.dto;


import java.util.List;

public class ReservationRequestDto {
    private Long userId;          // L'ID de l'utilisateur qui réserve
    private Long trajetId;        // L'ID du voyage sélectionné
    private int nombrePlace;      // Nombre total de places (Responsable + Accompagnateurs)
    private String nomResponsable; // Le nom de celui qui fait la réservation
    private List<String> nomsPassagers; // Liste des noms pour les autres billets (optionnel)

    public ReservationRequestDto() {}

    // Getters et Setters
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