package com.ipnet.dto;


import com.ipnet.enums.ModePaiement;
import lombok.Data;

@Data
public class PaiementRequestDto {
    private Long reservationId;
    private Double montantVerse;
    private String reference; 
    private ModePaiement modePaiement;
	public Long getReservationId() {
		return reservationId;
	}
	public void setReservationId(Long reservationId) {
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