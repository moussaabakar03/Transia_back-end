package com.ipnet.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.ipnet.enums.StatutVehicule;
import jakarta.persistence.*;
import com.ipnet.utils.BaseEntity;

@Entity
@Table(name = "Vehicule")
public class VehiculeEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "Marque", length = 150)
    private String marque;

    @Column(name = "Modele", length = 150)
    private String modele;

    @Column(name = "immatriculation", length = 150, unique = true)
    private String immatriculation;

    @Column(name = "Capacite", nullable = false)
    private int capacite;

    @Column(name = "capacite_soute", nullable = false)
    private int capaciteSoute;

    @Enumerated(EnumType.STRING)
    @Column(name = "Statut")
    private StatutVehicule statut;

    @Column(name = "Image", columnDefinition = "LONGTEXT")
    private String image;

    @Column(name = "kilometrage")
    private Double kilometrage = 0.0;

    @ManyToOne
    @JoinColumn(name = "ville_base_id")
    private VilleEntity villeBase;

    @ManyToOne
    @JoinColumn(name = "ville_actuelle_id")
    private VilleEntity villeActuelle;

    @ManyToOne
    @JoinColumn(name = "agence_id")
    private AgenceEntity agence;

    public VehiculeEntity() {}

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

    public VilleEntity getVilleBase() { return villeBase; }
    public void setVilleBase(VilleEntity villeBase) { this.villeBase = villeBase; }

    public VilleEntity getVilleActuelle() { return villeActuelle; }
    public void setVilleActuelle(VilleEntity villeActuelle) { this.villeActuelle = villeActuelle; }

    public Double getKilometrage() { return kilometrage; }
    public void setKilometrage(Double kilometrage) { this.kilometrage = kilometrage; }

    public AgenceEntity getAgence() { return agence; }
    public void setAgence(AgenceEntity agence) { this.agence = agence; }
}
