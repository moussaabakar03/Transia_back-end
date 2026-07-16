package com.ipnet.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class SuiviTrajetDto {

    private Long id;
    private String statut;

    private UUID trajetId;

    private String villeDepart;
    private String villeArrivee;

    private String chauffeurNom;
    private Long chauffeurId;

    private String vehicule;
    private String immatriculation;

    private String dateDepart;
    private LocalTime heureDepart;

    private LocalDateTime dateDemarrage;
    private LocalDateTime dateFin;
    private LocalDateTime derniereMiseAJour;

    private String message;

    private PositionGpsDto dernierePosition;

    public SuiviTrajetDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public UUID getTrajetId() {
        return trajetId;
    }

    public void setTrajetId(UUID trajetId) {
        this.trajetId = trajetId;
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

    public String getChauffeurNom() {
        return chauffeurNom;
    }

    public void setChauffeurNom(String chauffeurNom) {
        this.chauffeurNom = chauffeurNom;
    }

    public Long getChauffeurId() {
        return chauffeurId;
    }

    public void setChauffeurId(Long chauffeurId) {
        this.chauffeurId = chauffeurId;
    }

    public String getVehicule() {
        return vehicule;
    }

    public void setVehicule(String vehicule) {
        this.vehicule = vehicule;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public String getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(String dateDepart) {
        this.dateDepart = dateDepart;
    }

    public LocalTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public LocalDateTime getDateDemarrage() {
        return dateDemarrage;
    }

    public void setDateDemarrage(LocalDateTime dateDemarrage) {
        this.dateDemarrage = dateDemarrage;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public LocalDateTime getDerniereMiseAJour() {
        return derniereMiseAJour;
    }

    public void setDerniereMiseAJour(
            LocalDateTime derniereMiseAJour
    ) {
        this.derniereMiseAJour = derniereMiseAJour;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PositionGpsDto getDernierePosition() {
        return dernierePosition;
    }

    public void setDernierePosition(PositionGpsDto dernierePosition) {
        this.dernierePosition = dernierePosition;
    }
}