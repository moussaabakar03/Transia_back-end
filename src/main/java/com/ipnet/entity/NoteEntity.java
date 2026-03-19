package com.ipnet.entity;

import com.ipnet.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class NoteEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int valeur; // Note de 1 à 5

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", nullable = false)
    private TrajetEntity trajet;

    public NoteEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getValeur() { return valeur; }
    public void setValeur(int valeur) { this.valeur = valeur; }
    public TrajetEntity getTrajet() { return trajet; }
    public void setTrajet(TrajetEntity trajet) { this.trajet = trajet; }
}