package com.ipnet.entity;

import com.ipnet.utils.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "positions_gps",
        indexes = {
                @Index(
                        name = "idx_position_suivi_date",
                        columnList = "suivi_trajet_id,date_heure"
                )
        }
)
public class PositionGpsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column
    private Double vitesse;

    @Column
    private Double precisionGps;

    @Column
    private Double altitude;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "suivi_trajet_id", nullable = false)
    private SuiviTrajetEntity suiviTrajet;

    public PositionGpsEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getVitesse() {
        return vitesse;
    }

    public void setVitesse(Double vitesse) {
        this.vitesse = vitesse;
    }

    public Double getPrecisionGps() {
        return precisionGps;
    }

    public void setPrecisionGps(Double precisionGps) {
        this.precisionGps = precisionGps;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public SuiviTrajetEntity getSuiviTrajet() {
        return suiviTrajet;
    }

    public void setSuiviTrajet(SuiviTrajetEntity suiviTrajet) {
        this.suiviTrajet = suiviTrajet;
    }
}