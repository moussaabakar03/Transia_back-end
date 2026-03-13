package com.ipnet.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import com.ipnet.enums.StatutTrajet;

public class TrajetDto {

    private Long id;
    private String villeDepart;
    private String villeArrivee;
    private Double distance;
    private String dureeEstimee;
    private Double tarif;
    private LocalDate dateDepart;
    private LocalTime heureDepart;
    private StatutTrajet statut;
    private Long vehiculeId;

    public TrajetDto() {
    }

    public TrajetDto(Long id, String villeDepart, String villeArrivee, Double distance, 
                     String dureeEstimee, Double tarif, LocalDate dateDepart, 
                     LocalTime heureDepart, StatutTrajet statut, Long vehiculeId) {
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
        this.vehiculeId = vehiculeId;
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

    public Long getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(Long vehiculeId) {
        this.vehiculeId = vehiculeId;
    }
}