package com.ipnet.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.ipnet.enums.StatutCollecte;
import com.ipnet.security.model.User;
import com.ipnet.utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "demande_collecte")
public class DemandeCollecteEntity extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String adresseCollecte;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime dateHeureCollecte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCollecte statut;

    @ManyToOne
    @JoinColumn(name = "expediteur_id", nullable = false)
    private User expediteur;

    @ManyToOne
    @JoinColumn(name = "agence_id", nullable = false)
    private AgenceEntity agence;

    @ManyToOne
    @JoinColumn(name = "livreur_id", nullable = true)
    private User livreur;

    @ManyToOne
    @JoinColumn(name = "colis_id", nullable = true)
    private Colis colis;

    // Regroupement optionnel des collectes d'un livreur pour une journée/zone donnée.
    @ManyToOne
    @JoinColumn(name = "tournee_id", nullable = true)
    private Tournee tournee;

    public DemandeCollecteEntity() {
        this.statut = StatutCollecte.EN_ATTENTE;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getAdresseCollecte() { return adresseCollecte; }
    public void setAdresseCollecte(String adresseCollecte) { this.adresseCollecte = adresseCollecte; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getDateHeureCollecte() { return dateHeureCollecte; }
    public void setDateHeureCollecte(LocalDateTime dateHeureCollecte) { this.dateHeureCollecte = dateHeureCollecte; }

    public StatutCollecte getStatut() { return statut; }
    public void setStatut(StatutCollecte statut) { this.statut = statut; }

    public User getExpediteur() { return expediteur; }
    public void setExpediteur(User expediteur) { this.expediteur = expediteur; }

    public AgenceEntity getAgence() { return agence; }
    public void setAgence(AgenceEntity agence) { this.agence = agence; }

    public User getLivreur() { return livreur; }
    public void setLivreur(User livreur) { this.livreur = livreur; }

    public Colis getColis() { return colis; }
    public void setColis(Colis colis) { this.colis = colis; }

    public Tournee getTournee() { return tournee; }
    public void setTournee(Tournee tournee) { this.tournee = tournee; }
}
