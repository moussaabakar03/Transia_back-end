package com.ipnet.dto;

import com.ipnet.enums.StatutTrajet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class TrajetResponseDto {
    
    private UUID id;
    private VilleDto villeDepart;
    private VilleDto villeArrivee;
    private VehiculeDto vehicule;
    private Long chauffeurId;         
    private Double distance;
    private String dureeEstimee;
    private Double tarif;
    private LocalDate dateDepart;
    private LocalTime heureDepart;
    private StatutTrajet statut;

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public VilleDto getVilleDepart() { return villeDepart; }
    public void setVilleDepart(VilleDto villeDepart) { this.villeDepart = villeDepart; }

    public VilleDto getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(VilleDto villeArrivee) { this.villeArrivee = villeArrivee; }

    public VehiculeDto getVehicule() { return vehicule; }
    public void setVehicule(VehiculeDto vehicule) { this.vehicule = vehicule; }

    public Long getChauffeurId() { return chauffeurId; }
    public void setChauffeurId(Long chauffeurId) { this.chauffeurId = chauffeurId; }

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
}