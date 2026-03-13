package com.ipnet.dto;

import com.ipnet.enums.StatutReservation;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationResponseDto {
    private Long id;
    private LocalDateTime dateReservation;
    private StatutReservation statut;
    private int nombrePlace;
    private Long trajetId;
    private List<BilletDto> billets;
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDateTime getDateReservation() {
		return dateReservation;
	}
	public void setDateReservation(LocalDateTime dateReservation) {
		this.dateReservation = dateReservation;
	}
	public StatutReservation getStatut() {
		return statut;
	}
	public void setStatut(StatutReservation statut) {
		this.statut = statut;
	}
	public int getNombrePlace() {
		return nombrePlace;
	}
	public void setNombrePlace(int nombrePlace) {
		this.nombrePlace = nombrePlace;
	}
	public Long getTrajetId() {
		return trajetId;
	}
	public void setTrajetId(Long trajetId) {
		this.trajetId = trajetId;
	}
	public List<BilletDto> getBillets() {
		return billets;
	}
	public void setBillets(List<BilletDto> billets) {
		this.billets = billets;
	}

    // Getters et Setters...
    
}