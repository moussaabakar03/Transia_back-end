package com.ipnet.dto;

import com.ipnet.enums.StatutBillet;
import java.time.LocalDateTime;

public class BilletDto {
    private Long id;
    private String qrCode;
    private String nomPassager;
    private StatutBillet statut;
    private LocalDateTime dateEmission;

    public BilletDto() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

