package com.ipnet.dto;

import com.ipnet.enums.StatutTrajet;
import java.time.LocalDate;
import java.time.LocalTime;

public class TrajetResponseDto {
    private Long id;
    private String villeDepartNom;
    private String villeArriveeNom;
    private String vehiculeImmatriculation;
    private Double distance;
    private String dureeEstimee;
    private Double tarif;
    private LocalDate dateDepart;
    private LocalTime heureDepart;
    private StatutTrajet statut;

    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVilleDepartNom() { return villeDepartNom; }
    public void setVilleDepartNom(String villeDepartNom) { this.villeDepartNom = villeDepartNom; }
    public String getVilleArriveeNom() { return villeArriveeNom; }
    public void setVilleArriveeNom(String villeArriveeNom) { this.villeArriveeNom = villeArriveeNom; }
    public String getVehiculeImmatriculation() { return vehiculeImmatriculation; }
    public void setVehiculeImmatriculation(String vehiculeImmatriculation) { this.vehiculeImmatriculation = vehiculeImmatriculation; }
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