package com.ipnet.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.ipnet.enums.StatutColis;
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
@Table(name = "historique_colis")
public class HistoriqueColis extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "colis_id", nullable = false)
    private Colis colis;

    @Enumerated(EnumType.STRING)
    @Column
    private StatutColis ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutColis nouveauStatut;

    @Column(nullable = false)
    private LocalDateTime dateChangement;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private User utilisateur;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    public HistoriqueColis() {
        this.dateChangement = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Colis getColis() {
        return colis;
    }

    public void setColis(Colis colis) {
        this.colis = colis;
    }

    public StatutColis getAncienStatut() {
        return ancienStatut;
    }

    public void setAncienStatut(StatutColis ancienStatut) {
        this.ancienStatut = ancienStatut;
    }

    public StatutColis getNouveauStatut() {
        return nouveauStatut;
    }

    public void setNouveauStatut(StatutColis nouveauStatut) {
        this.nouveauStatut = nouveauStatut;
    }

    public LocalDateTime getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
