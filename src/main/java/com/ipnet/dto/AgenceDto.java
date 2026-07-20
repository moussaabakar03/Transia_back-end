package com.ipnet.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AgenceDto {

    private UUID id;
    private String nom;
    private UUID villeId;
    private String villeNom;
    private String adresse;
    private String telephone;
    private String email;
    private Double latitude;
    private Double longitude;
    private Boolean statut;
    private List<String> photos = new ArrayList<>();

    public AgenceDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public UUID getVilleId() { return villeId; }
    public void setVilleId(UUID villeId) { this.villeId = villeId; }

    public String getVilleNom() { return villeNom; }
    public void setVilleNom(String villeNom) { this.villeNom = villeNom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Boolean getStatut() { return statut; }
    public void setStatut(Boolean statut) { this.statut = statut; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }
}
