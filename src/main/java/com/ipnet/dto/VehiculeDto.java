package com.ipnet.dto;

import java.util.UUID;

import com.ipnet.enums.StatutVehicule;

public class VehiculeDto {

	private UUID id;
	
	private String marque;
	
	private String modele;
	
	private String immatriculation;
	
	private int capacite;
	
	private StatutVehicule statut;

	private String image;
	

	public VehiculeDto() {
	}
	


	public VehiculeDto(UUID id, String marque, String modele, String immatriculation, int capacite,
			StatutVehicule statut, String image) {
		super();
		this.id = id;
		this.marque = marque;
		this.modele = modele;
		this.immatriculation = immatriculation;
		this.capacite = capacite;
		this.statut = statut;
		this.image = image;
	}



	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public String getMarque() {
		return marque;
	}


	public void setMarque(String marque) {
		this.marque = marque;
	}


	public String getModele() {
		return modele;
	}


	public void setModele(String modele) {
		this.modele = modele;
	}


	public String getImmatriculation() {
		return immatriculation;
	}


	public void setImmatriculation(String immatriculation) {
		this.immatriculation = immatriculation;
	}


	public int getCapacite() {
		return capacite;
	}


	public void setCapacite(int capacite) {
		this.capacite = capacite;
	}


	public StatutVehicule getStatut() {
		return statut;
	}


	public void setStatut(StatutVehicule statut) {
		this.statut = statut;
	}


	public String getImage() {
		return image;
	}


	public void setImage(String image) {
		this.image = image;
	}
	
}
