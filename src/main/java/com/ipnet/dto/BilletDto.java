package com.ipnet.dto;

import com.ipnet.enums.StatutBillet;
import java.time.LocalDateTime;
import java.util.UUID;

public class BilletDto {
    private UUID id;
    private String qrCode;
    private String nomPassager;
    private StatutBillet statut;
    private LocalDateTime dateEmission;
    private String numeroSiege;
    private UUID reservationId;
    private String trajetInfo;
    private UUID trajetId;
    private String dateDepart;
    private String heureDepart;

    
    public BilletDto() {}

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

	public String getNomPassager() {
		return nomPassager;
	}

	public void setNomPassager(String nomPassager) {
		this.nomPassager = nomPassager;
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
	
    public String getNumeroSiege() { return numeroSiege; }
    public void setNumeroSiege(String numeroSiege) { this.numeroSiege = numeroSiege; }

    public UUID getReservationId() { return reservationId; }
    public void setReservationId(UUID reservationId) { this.reservationId = reservationId; }

    public String getTrajetInfo() { return trajetInfo; }
    public void setTrajetInfo(String trajetInfo) { this.trajetInfo = trajetInfo; }

    public UUID getTrajetId() { return trajetId; }
    public void setTrajetId(UUID trajetId) { this.trajetId = trajetId; }

    public String getDateDepart() { return dateDepart; }
    public void setDateDepart(String dateDepart) { this.dateDepart = dateDepart; }

    public String getHeureDepart() { return heureDepart; }
    public void setHeureDepart(String heureDepart) { this.heureDepart = heureDepart; }
}

