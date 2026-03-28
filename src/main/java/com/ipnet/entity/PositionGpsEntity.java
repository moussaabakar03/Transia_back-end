package com.ipnet.entity;

import com.ipnet.utils.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions_gps")
public class PositionGpsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    // Relation avec le Suivi (pour savoir à quel voyage appartient ce point)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suivi_trajet_id")
    private SuiviTrajetEntity suiviTrajet;

    public PositionGpsEntity() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public SuiviTrajetEntity getSuiviTrajet() { return suiviTrajet; }
    public void setSuiviTrajet(SuiviTrajetEntity suiviTrajet) { this.suiviTrajet = suiviTrajet; }
}