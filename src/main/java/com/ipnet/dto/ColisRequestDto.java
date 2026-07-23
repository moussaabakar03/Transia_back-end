package com.ipnet.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ipnet.enums.ModeDepot;
import com.ipnet.enums.ModeRemise;

public class ColisRequestDto {
    private UUID expediteurId;
    private String nomDestinataire;
    private String adresseDestinataire;
    private String telephoneDestinataire;
    private Double poids;
    private Double longueur;
    private Double largeur;
    private Double hauteur;
    private String remarques;
    private ModeDepot modeDepot;
    private String adresseCollecte;
    private String telephoneCollecte;
    private LocalDateTime dateHeureCollecteSouhaitee;
    private Double latitudeDestinataire;
    private Double longitudeDestinataire;
    private Double latitudeCollecte;
    private Double longitudeCollecte;
    private UUID villeDepartId;
    private UUID villeArriveeId;
    private UUID trajetId;
    private ModeRemise modeRemise;

    public UUID getExpediteurId() {
        return expediteurId;
    }

    public void setExpediteurId(UUID expediteurId) {
        this.expediteurId = expediteurId;
    }

    public String getNomDestinataire() {
        return nomDestinataire;
    }

    public void setNomDestinataire(String nomDestinataire) {
        this.nomDestinataire = nomDestinataire;
    }

    public String getAdresseDestinataire() {
        return adresseDestinataire;
    }

    public void setAdresseDestinataire(String adresseDestinataire) {
        this.adresseDestinataire = adresseDestinataire;
    }

    public String getTelephoneDestinataire() {
        return telephoneDestinataire;
    }

    public void setTelephoneDestinataire(String telephoneDestinataire) {
        this.telephoneDestinataire = telephoneDestinataire;
    }

    public Double getPoids() {
        return poids;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    public Double getLongueur() {
        return longueur;
    }

    public void setLongueur(Double longueur) {
        this.longueur = longueur;
    }

    public Double getLargeur() {
        return largeur;
    }

    public void setLargeur(Double largeur) {
        this.largeur = largeur;
    }

    public Double getHauteur() {
        return hauteur;
    }

    public void setHauteur(Double hauteur) {
        this.hauteur = hauteur;
    }

    public String getRemarques() {
        return remarques;
    }

    public void setRemarques(String remarques) {
        this.remarques = remarques;
    }

    public ModeDepot getModeDepot() {
        return modeDepot;
    }

    public void setModeDepot(ModeDepot modeDepot) {
        this.modeDepot = modeDepot;
    }

    public String getAdresseCollecte() {
        return adresseCollecte;
    }

    public void setAdresseCollecte(String adresseCollecte) {
        this.adresseCollecte = adresseCollecte;
    }

    public String getTelephoneCollecte() {
        return telephoneCollecte;
    }

    public void setTelephoneCollecte(String telephoneCollecte) {
        this.telephoneCollecte = telephoneCollecte;
    }

    public LocalDateTime getDateHeureCollecteSouhaitee() {
        return dateHeureCollecteSouhaitee;
    }

    public void setDateHeureCollecteSouhaitee(LocalDateTime dateHeureCollecteSouhaitee) {
        this.dateHeureCollecteSouhaitee = dateHeureCollecteSouhaitee;
    }

    public Double getLatitudeDestinataire() {
        return latitudeDestinataire;
    }

    public void setLatitudeDestinataire(Double latitudeDestinataire) {
        this.latitudeDestinataire = latitudeDestinataire;
    }

    public Double getLongitudeDestinataire() {
        return longitudeDestinataire;
    }

    public void setLongitudeDestinataire(Double longitudeDestinataire) {
        this.longitudeDestinataire = longitudeDestinataire;
    }

    public Double getLatitudeCollecte() {
        return latitudeCollecte;
    }

    public void setLatitudeCollecte(Double latitudeCollecte) {
        this.latitudeCollecte = latitudeCollecte;
    }

    public Double getLongitudeCollecte() {
        return longitudeCollecte;
    }

    public void setLongitudeCollecte(Double longitudeCollecte) {
        this.longitudeCollecte = longitudeCollecte;
    }

    public UUID getVilleDepartId() { return villeDepartId; }
    public void setVilleDepartId(UUID villeDepartId) { this.villeDepartId = villeDepartId; }

    public UUID getVilleArriveeId() { return villeArriveeId; }
    public void setVilleArriveeId(UUID villeArriveeId) { this.villeArriveeId = villeArriveeId; }

    public UUID getTrajetId() { return trajetId; }
    public void setTrajetId(UUID trajetId) { this.trajetId = trajetId; }

    public ModeRemise getModeRemise() { return modeRemise; }
    public void setModeRemise(ModeRemise modeRemise) { this.modeRemise = modeRemise; }
}
