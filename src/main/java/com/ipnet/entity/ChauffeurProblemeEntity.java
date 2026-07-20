package com.ipnet.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.ipnet.security.model.User;
import com.ipnet.utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "chauffeur_problemes")
public class ChauffeurProblemeEntity extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trajet_id", nullable = false)
    private TrajetEntity trajet;

    @ManyToOne
    @JoinColumn(name = "chauffeur_id", nullable = false)
    private User chauffeur;

    @Column(nullable = false, length = 150)
    private String typeProbleme;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String statut;

    public ChauffeurProblemeEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TrajetEntity getTrajet() {
        return trajet;
    }

    public void setTrajet(TrajetEntity trajet) {
        this.trajet = trajet;
    }

    public User getChauffeur() {
        return chauffeur;
    }

    public void setChauffeur(User chauffeur) {
        this.chauffeur = chauffeur;
    }

    public String getTypeProbleme() {
        return typeProbleme;
    }

    public void setTypeProbleme(String typeProbleme) {
        this.typeProbleme = typeProbleme;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}