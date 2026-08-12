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
    private UUID chauffeurId;
    private String chauffeurNom;
    private UUID agenceId;
    private String agenceNom;
    private AgenceDto agenceDepart;
    private AgenceDto agenceArrivee;
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

    public UUID getChauffeurId() { return chauffeurId; }
    public void setChauffeurId(UUID chauffeurId) { this.chauffeurId = chauffeurId; }

    public String getChauffeurNom() { return chauffeurNom; }
    public void setChauffeurNom(String chauffeurNom) { this.chauffeurNom = chauffeurNom; }

    public UUID getAgenceId() {
        return agenceDepart != null ? agenceDepart.getId() : agenceId;
    }
    public void setAgenceId(UUID agenceId) { this.agenceId = agenceId; }

    public String getAgenceNom() {
        return agenceDepart != null ? agenceDepart.getNom() : agenceNom;
    }
    public void setAgenceNom(String agenceNom) { this.agenceNom = agenceNom; }

    public AgenceDto getAgenceDepart() { return agenceDepart; }
    public void setAgenceDepart(AgenceDto agenceDepart) {
        this.agenceDepart = agenceDepart;
        if (agenceDepart != null) {
            this.agenceId = agenceDepart.getId();
            this.agenceNom = agenceDepart.getNom();
        }
    }

    public AgenceDto getAgenceArrivee() { return agenceArrivee; }
    public void setAgenceArrivee(AgenceDto agenceArrivee) { this.agenceArrivee = agenceArrivee; }

    public UUID getAgenceDepartId() { return agenceDepart != null ? agenceDepart.getId() : agenceId; }
    public String getAgenceDepartNom() { return agenceDepart != null ? agenceDepart.getNom() : agenceNom; }
    public String getAgenceDepartAdresse() { return agenceDepart != null ? agenceDepart.getAdresse() : null; }

    public UUID getAgenceArriveeId() { return agenceArrivee != null ? agenceArrivee.getId() : null; }
    public String getAgenceArriveeNom() { return agenceArrivee != null ? agenceArrivee.getNom() : null; }
    public String getAgenceArriveeAdresse() { return agenceArrivee != null ? agenceArrivee.getAdresse() : null; }

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