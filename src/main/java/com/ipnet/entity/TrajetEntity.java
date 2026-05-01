package com.ipnet.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.ipnet.enums.StatutTrajet;
import com.ipnet.security.model.User;
import com.ipnet.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name="Trajet")
public class TrajetEntity extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    // Remplacement du String par une relation vers VilleEntity
    @ManyToOne
    @JoinColumn(name = "ville_depart_id", nullable = false)
    private VilleEntity villeDepart;

    // Remplacement du String par une relation vers VilleEntity
    @ManyToOne
    @JoinColumn(name = "ville_arrivee_id", nullable = false)
    private VilleEntity villeArrivee;

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
    @JoinColumn(name = "chauffeur_id")
    private User chauffeur;

    // Constructeur vide
    public TrajetEntity() {
    }

    // Constructeur complet mis à jour
    public TrajetEntity(UUID id, VilleEntity villeDepart, VilleEntity villeArrivee, Double distance,
            String dureeEstimee, Double tarif, LocalDate dateDepart,
            LocalTime heureDepart, StatutTrajet statut, VehiculeEntity vehicule,
            User chauffeur) {
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
			this.chauffeur = chauffeur;
			}
    // Getters et Setters mis à jour
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public VilleEntity getVilleDepart() { return villeDepart; }
    public void setVilleDepart(VilleEntity villeDepart) { this.villeDepart = villeDepart; }

    public VilleEntity getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(VilleEntity villeArrivee) { this.villeArrivee = villeArrivee; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public String getDureeEstimee() { return dureeEstimee; }
    public void setDureeEstimee(String dureeEstimee) { this.dureeEstimee = dureeEstimee; }

    public Double getTarif() { return tarif; }
    public void setTarif(Double tarif) { this.tarif = tarif; }

    public LocalDate getDateDepart() { return dateDepart; }
    public void setDateDepart(LocalDate dateDepart) { this.dateDepart = dateDepart; }

    public LocalTime getHeureDepart() { return heureDepart; }
    public void setHeureDepart(LocalTime heureDepart) { this.heureDepart = heureDepart; }

    public StatutTrajet getStatut() { return statut; }
    public void setStatut(StatutTrajet statut) { this.statut = statut; }

    public VehiculeEntity getVehicule() { return vehicule; }
    public void setVehicule(VehiculeEntity vehicule) { this.vehicule = vehicule; }
    
    public User getChauffeur() { return chauffeur; }
    public void setChauffeur(User chauffeur) { this.chauffeur = chauffeur; }
}