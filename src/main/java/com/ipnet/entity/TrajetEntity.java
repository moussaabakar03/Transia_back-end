package com.ipnet.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.ipnet.enums.StatutTrajet;
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
@Table(name="Trajet")
public class TrajetEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="VilleDepart", nullable=false, length=150)
    private String villeDepart;

    @Column(name="VilleArrivee", nullable=false, length=150)
    private String villeArrivee;

    @Column(name="Distance")
    private Double distance;

    @Column(name="DureeEstimee", length=100)
    private String dureeEstimee;

    @Column(name="Tarif", nullable=false)
    private Double tarif;

    @Column(name="DateDepart", nullable=false)
    private LocalDate dateDepart;

    @Column(name="HeureDepart", nullable=false)
    private LocalTime heureDepart;

    @Enumerated(EnumType.STRING)
    @Column(name="Statut")
    private StatutTrajet statut;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private VehiculeEntity vehicule;


    @ManyToOne
    @JoinColumn(name = "ville_id") 
    private VilleEntity ville;


    public TrajetEntity() {
    }


    public TrajetEntity(Long id, String villeDepart, String villeArrivee, Double distance, 
                        String dureeEstimee, Double tarif, LocalDate dateDepart, 
                        LocalTime heureDepart, StatutTrajet statut, VehiculeEntity vehicule, VilleEntity ville) {
        super();
        this.id = id;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.distance = distance;
        this.dureeEstimee = dureeEstimee;
        this.tarif = tarif;
        this.dateDepart = dateDepart;
        this.heureDepart = heureDepart;
        this.statut = statut;
        this.vehicule = vehicule;
        this.ville = ville;
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

    public String getVilleArrivee() {
        return villeArrivee;
    }

    public void setVilleArrivee(String villeArrivee) {
        this.villeArrivee = villeArrivee;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getDureeEstimee() {
        return dureeEstimee;
    }

    public void setDureeEstimee(String dureeEstimee) {
        this.dureeEstimee = dureeEstimee;
    }

    public Double getTarif() {
        return tarif;
    }

    public void setTarif(Double tarif) {
        this.tarif = tarif;
    }

    public LocalDate getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDate dateDepart) {
        this.dateDepart = dateDepart;
    }

    public LocalTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public StatutTrajet getStatut() {
        return statut;
    }

    public void setStatut(StatutTrajet statut) {
        this.statut = statut;
    }

    public VehiculeEntity getVehicule() {
        return vehicule;
    }

    public void setVehicule(VehiculeEntity vehicule) {
        this.vehicule = vehicule;
    }

    public VilleEntity getVille() {
        return ville;
    }

    public void setVille(VilleEntity ville) {
        this.ville = ville;
    }
}