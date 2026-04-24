package com.ipnet.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.ipnet.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class NoteEntity extends BaseEntity {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private int valeur; // Note de 1 à 5

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", nullable = false)
    private TrajetEntity trajet;

    public NoteEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public int getValeur() { return valeur; }
    public void setValeur(int valeur) { this.valeur = valeur; }
    public TrajetEntity getTrajet() { return trajet; }
    public void setTrajet(TrajetEntity trajet) { this.trajet = trajet; }
}
