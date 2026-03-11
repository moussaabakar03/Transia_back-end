package com.ipnet.entity;

import java.time.LocalDateTime;

import com.ipnet.enums.StatutTransport;
import com.ipnet.utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Transport")
public class TransportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="ville_depart", nullable=false)
    private String villeDepart;

    @Column(name="ville_destination", nullable=false)
    private String villeDestination;

    @Column(name="date_heure_depart", nullable=false)
    private LocalDateTime dateHeureDepart;

    @Column(name="prix", nullable=false)
    private Double prix;

    @Enumerated(EnumType.STRING)
    private StatutTransport statut;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private VehiculeEntity vehicule;

    public TransportEntity() {
    }

    public TransportEntity(Long id, String villeDepart, String villeDestination, LocalDateTime dateHeureDepart,
                           Double prix, StatutTransport statut, VehiculeEntity vehicule) {
        super();
        this.id = id;
        this.villeDepart = villeDepart;
        this.villeDestination = villeDestination;
        this.dateHeureDepart = dateHeureDepart;
        this.prix = prix;
        this.statut = statut;
        this.vehicule = vehicule;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVilleDepart() {
        return villeDepart;
    }

    public void setVilleDepart(String villeDepart) {
        this.villeDepart = villeDepart;
    }

    public String getVilleDestination() {
        return villeDestination;
    }

    public void setVilleDestination(String villeDestination) {
        this.villeDestination = villeDestination;
    }

    public LocalDateTime getDateHeureDepart() {
        return dateHeureDepart;
    }

    public void setDateHeureDepart(LocalDateTime dateHeureDepart) {
        this.dateHeureDepart = dateHeureDepart;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public StatutTransport getStatut() {
        return statut;
    }

    public void setStatut(StatutTransport statut) {
        this.statut = statut;
    }

    public VehiculeEntity getVehicule() {
        return vehicule;
    }

    public void setVehicule(VehiculeEntity vehicule) {
        this.vehicule = vehicule;
    }
}