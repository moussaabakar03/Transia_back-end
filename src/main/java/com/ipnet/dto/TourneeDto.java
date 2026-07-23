package com.ipnet.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.ipnet.security.dto.UserDTO;

public class TourneeDto {
    private UUID id;
    private LocalDate dateTournee;
    private UUID livreurId;
    private UserDTO livreur;
    private String zone;
    private List<ColisDto> colis;
    private String statut;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getDateTournee() {
        return dateTournee;
    }

    public void setDateTournee(LocalDate dateTournee) {
        this.dateTournee = dateTournee;
    }

    public UUID getLivreurId() {
        return livreurId;
    }

    public void setLivreurId(UUID livreurId) {
        this.livreurId = livreurId;
    }

    public UserDTO getLivreur() {
        return livreur;
    }

    public void setLivreur(UserDTO livreur) {
        this.livreur = livreur;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public List<ColisDto> getColis() {
        return colis;
    }

    public void setColis(List<ColisDto> colis) {
        this.colis = colis;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
