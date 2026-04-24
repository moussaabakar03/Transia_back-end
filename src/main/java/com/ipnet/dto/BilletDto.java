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

    
}

