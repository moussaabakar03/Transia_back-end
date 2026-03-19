package com.ipnet.dto;

import com.ipnet.enums.StatutTrajet;
import java.time.LocalDate;
import java.time.LocalTime;

public class TrajetRequestDto {
    private Long villeDepartId;
    private Long villeArriveeId;
    private Long vehiculeId;
    private Double distance;
    private String dureeEstimee;
    private Double tarif;
    private LocalDate dateDepart;
    private LocalTime heureDepart;
    private StatutTrajet statut;

    // Getters et Setters
    public Long getVilleDepartId() { return villeDepartId; }
    public void setVilleDepartId(Long villeDepartId) { this.villeDepartId = villeDepartId; }
    public Long getVilleArriveeId() { return villeArriveeId; }
    public void setVilleArriveeId(Long villeArriveeId) { this.villeArriveeId = villeArriveeId; }
    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }
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