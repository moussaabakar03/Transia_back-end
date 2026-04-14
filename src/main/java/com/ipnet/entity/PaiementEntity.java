package com.ipnet.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.ipnet.enums.ModePaiement;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PaiementEntity {
    @Id
    @UuidGenerator
    private UUID id;

    private String reference; // Ex: "REC-2026-001"
    private Double montant;
    private LocalDateTime datePaiement;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement; // ESPECES, TMONEY, FLOOZ

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Double getMontant() {
		return montant;
	}

	public void setMontant(Double montant) {
		this.montant = montant;
	}

	public LocalDateTime getDatePaiement() {
		return datePaiement;
	}

	public void setDatePaiement(LocalDateTime datePaiement) {
		this.datePaiement = datePaiement;
	}

	public ModePaiement getModePaiement() {
		return modePaiement;
	}

	public void setModePaiement(ModePaiement modePaiement) {
		this.modePaiement = modePaiement;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}
    
    
}
