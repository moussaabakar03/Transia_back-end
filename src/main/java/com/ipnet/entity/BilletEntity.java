package com.ipnet.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.ipnet.enums.StatutBillet;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class BilletEntity {
    @Id
    @UuidGenerator
    private UUID id;
    private String qrCode; // Identifiant unique pour le QR
    private StatutBillet statut;
    private LocalDateTime dateEmission;
    private String nomPassager;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public StatutBillet getStatut() {
		return statut;
	}

	public void setStatut(StatutBillet statut) {
		this.statut = statut;
	}

	public LocalDateTime getDateEmission() {
		return dateEmission;
	}

	public void setDateEmission(LocalDateTime dateEmission) {
		this.dateEmission = dateEmission;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public String getNomPassager() {
		return nomPassager;
	}

	public void setNomPassager(String nomPassager) {
		this.nomPassager = nomPassager;
	}
    
	
    
    
}
