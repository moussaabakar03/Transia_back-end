package com.ipnet.dto;

import com.ipnet.enums.StatutTrajet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class TrajetRequestDto {

    private UUID villeDepartId;
    private UUID villeArriveeId;
    private UUID vehiculeId;
    private Long chauffeurId;           // ← AJOUTÉ
    private Double distance;
    private String dureeEstimee;
    private Double tarif;
    private LocalDate dateDepart;
    private LocalTime heureDepart;
    private StatutTrajet statut;

    // Getters & Setters pour chaque champ
    public UUID getVilleDepartId() { return villeDepartId; }
    public void setVilleDepartId(UUID villeDepartId) { this.villeDepartId = villeDepartId; }

    public UUID getVilleArriveeId() { return villeArriveeId; }
    public void setVilleArriveeId(UUID villeArriveeId) { this.villeArriveeId = villeArriveeId; }

    public UUID getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(UUID vehiculeId) { this.vehiculeId = vehiculeId; }

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
