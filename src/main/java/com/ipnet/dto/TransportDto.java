package com.ipnet.dto;

import java.time.LocalDateTime;

import com.ipnet.enums.StatutTransport;

public class TransportDto {

    private Long id;
    private String villeDepart;
    private String villeDestination;
    private LocalDateTime dateHeureDepart;
    private Double prix;
    private StatutTransport statut;
    private Long vehiculeId;


    public TransportDto() {
    }

    
    public TransportDto(Long id, String villeDepart, String villeDestination, LocalDateTime dateHeureDepart,
                        Double prix, StatutTransport statut, Long vehiculeId) {
        this.id = id;
        this.villeDepart = villeDepart;
        this.villeDestination = villeDestination;
        this.dateHeureDepart = dateHeureDepart;
        this.prix = prix;
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

    public Long getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(Long vehiculeId) {
        this.vehiculeId = vehiculeId;
    }
}