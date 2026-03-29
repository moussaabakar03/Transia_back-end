package com.ipnet.entity;

import com.ipnet.enums.StatutVehicule;
import jakarta.persistence.*;
import com.ipnet.utils.BaseEntity;

@Entity
@Table(name="Vehicule")
public class VehiculeEntity extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="Marque", nullable=true, length=150)
	private String marque;
	
	@Column(name="Modele", nullable=true, length=150)
	private String modele;
	
	@Column(name="immatriculation", nullable=true, length=150)
	private String immatriculation;
	
	@Column(name="Capacite", nullable=false)
	private int capacite;
	
	@Enumerated(EnumType.STRING) 
	@Column(name="Statut")
	private StatutVehicule statut;
	
	@Column(name="Image", nullable=true, length=150)
	private String image;
	

	public VehiculeEntity() {
	}
	


	public VehiculeEntity(Long id, String marque, String modele, String immatriculation, int capacite,
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



	public Long getId() {
		return id;
	}


	public void setId(Long id) {
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
