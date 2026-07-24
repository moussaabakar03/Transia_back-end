package com.ipnet.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.ipnet.security.model.User;
import com.ipnet.utils.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tournee")
public class Tournee extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private LocalDate dateTournee;

    @ManyToOne
    @JoinColumn(name = "livreur_id", nullable = false)
    private User livreur;

    @Column
    private String zone;

    @OneToMany(mappedBy = "tournee", cascade = CascadeType.ALL)
    private List<DemandeCollecteEntity> demandesCollecte = new ArrayList<>();

    @Column
    private String statut;

    public Tournee() {
        this.statut = "PLANIFIEE";
    }

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

    public User getLivreur() {
        return livreur;
    }

    public void setLivreur(User livreur) {
        this.livreur = livreur;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public List<DemandeCollecteEntity> getDemandesCollecte() {
        return demandesCollecte;
    }

    public void setDemandesCollecte(List<DemandeCollecteEntity> demandesCollecte) {
        this.demandesCollecte = demandesCollecte;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
