package com.ipnet.entity;

import com.ipnet.utils.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suivis_trajets")
public class SuiviTrajetEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // État du trajet (on peut utiliser une String ou une Enum)
    private String statut; // "PROGRAMME", "EN_ROUTE", "ARRIVE", "ANNULE"

    @OneToOne
    @JoinColumn(name = "trajet_id", nullable = false)
    private TrajetEntity trajet;

    @OneToMany(mappedBy = "suiviTrajet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PositionGpsEntity> historiquePositions = new ArrayList<>();

    public SuiviTrajetEntity() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public TrajetEntity getTrajet() { return trajet; }
    public void setTrajet(TrajetEntity trajet) { this.trajet = trajet; }
    public List<PositionGpsEntity> getHistoriquePositions() { return historiquePositions; }
    public void setHistoriquePositions(List<PositionGpsEntity> historiquePositions) { this.historiquePositions = historiquePositions; }
}