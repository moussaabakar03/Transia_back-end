package com.ipnet.dto;

import java.util.UUID;

import com.ipnet.enums.ModePaiement;
import lombok.Data;


@Data
public class PaiementRequestDto {


    private UUID id;
    private UUID reservationId;
    private Double montantVerse;
    private String reference;
    private ModePaiement modePaiement;


	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public UUID getReservationId() {
		return reservationId;
	}
	public void setReservationId(UUID reservationId) {
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
