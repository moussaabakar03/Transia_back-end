package com.ipnet.dto;

import com.ipnet.entity.Reservation;
import com.ipnet.enums.ModePaiement;
import lombok.Data;


@Data
public class PaiementRequestDto {
	
	
    private Long id;
    private Reservation reservationId;
    private Double montantVerse;
    private String reference; 
    private ModePaiement modePaiement;

    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Reservation getReservationId() {
		return reservationId;
	}
	public void setReservationId(Reservation reservationId) {
		this.reservationId = reservationId;
	}
	public Double getMontantVerse() {
		return montantVerse;
	}
	public void setMontantVerse(Double montantVerse) {
		this.montantVerse = montantVerse;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public ModePaiement getModePaiement() {
		return modePaiement;
	}
	public void setModePaiement(ModePaiement modePaiement) {
		this.modePaiement = modePaiement;
	}
    
    
    
}