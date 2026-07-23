package com.ipnet.dto;

import java.util.UUID;

import com.ipnet.enums.StatutVehicule;

public class VehiculeDto {

    private UUID id;
    private String marque;
    private String modele;
    private String immatriculation;
    private int capacite;
    private int capaciteSoute;
    private StatutVehicule statut;
    private String image;

    private UUID villeBaseId;
    private String villeBaseNom;
    private UUID villeActuelleId;
    private String villeActuelleNom;

    public VehiculeDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public int getCapaciteSoute() { return capaciteSoute; }
    public void setCapaciteSoute(int capaciteSoute) { this.capaciteSoute = capaciteSoute; }

    public StatutVehicule getStatut() { return statut; }
    public void setStatut(StatutVehicule statut) { this.statut = statut; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public UUID getVilleBaseId() { return villeBaseId; }
    public void setVilleBaseId(UUID villeBaseId) { this.villeBaseId = villeBaseId; }

    public String getVilleBaseNom() { return villeBaseNom; }
    public void setVilleBaseNom(String villeBaseNom) { this.villeBaseNom = villeBaseNom; }

    public UUID getVilleActuelleId() { return villeActuelleId; }
    public void setVilleActuelleId(UUID villeActuelleId) { this.villeActuelleId = villeActuelleId; }

    public String getVilleActuelleNom() { return villeActuelleNom; }
    public void setVilleActuelleNom(String villeActuelleNom) { this.villeActuelleNom = villeActuelleNom; }
}
