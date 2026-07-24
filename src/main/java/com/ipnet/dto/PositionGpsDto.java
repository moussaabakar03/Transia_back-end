package com.ipnet.dto;

import java.time.LocalDateTime;

public class PositionGpsDto {

    private Long id;
    private Double latitude;
    private Double longitude;
    private Double vitesse;
    private Double precisionGps;
    private Double altitude;
    private LocalDateTime dateHeure;
    private Long suiviTrajetId;

    public PositionGpsDto() {
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

    public Long getSuiviTrajetId() {
        return suiviTrajetId;
    }

    public void setSuiviTrajetId(Long suiviTrajetId) {
        this.suiviTrajetId = suiviTrajetId;
    }
}