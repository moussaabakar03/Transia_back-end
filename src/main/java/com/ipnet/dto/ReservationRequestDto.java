package com.ipnet.dto;


import java.util.List;
import java.util.UUID;

import com.ipnet.enums.TypeReservation;

public class ReservationRequestDto {
    private Long userId;          
    private UUID trajetId;       
    private int nombrePlace;     
    private String nomResponsable; 
    private List<String> nomsPassagers; 
    private TypeReservation typeReservation;


    public ReservationRequestDto() {}

    // Getters et Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public UUID getTrajetId() { return trajetId; }
    public void setTrajetId(UUID trajetId) { this.trajetId = trajetId; }

    public int getNombrePlace() { return nombrePlace; }
    public void setNombrePlace(int nombrePlace) { this.nombrePlace = nombrePlace; }

    public String getNomResponsable() { return nomResponsable; }
    public void setNomResponsable(String nomResponsable) { this.nomResponsable = nomResponsable; }

    public List<String> getNomsPassagers() { return nomsPassagers; }
    public void setNomsPassagers(List<String> nomsPassagers) { this.nomsPassagers = nomsPassagers; }

	public TypeReservation getTypeReservation() {
		return typeReservation;
	}

	public void setTypeReservation(TypeReservation typeReservation) {
		this.typeReservation = typeReservation;
	}
    
}