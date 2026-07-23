package com.ipnet.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ipnet.enums.ModeDepot;
import com.ipnet.enums.ModeRemise;
import com.ipnet.enums.StatutColis;
import com.ipnet.security.dto.UserDTO;

public class ColisDto {
    private UUID id;
    private String numeroSuivi;
    private UUID expediteurId;
    private UserDTO expediteur;
    private String nomDestinataire;
    private String adresseDestinataire;
    private String telephoneDestinataire;
    private Double poids;
    private Double longueur;
    private Double largeur;
    private Double hauteur;
    private StatutColis statut;
    private UUID livreurId;
    private UserDTO livreur;
    private String remarques;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLivraison;
    private ModeDepot modeDepot;
    private String adresseCollecte;
    private String telephoneCollecte;
    private LocalDateTime dateHeureCollecteSouhaitee;
    private Double latitudeDestinataire;
    private Double longitudeDestinataire;
    private Double latitudeCollecte;
    private Double longitudeCollecte;
    private List<HistoriqueColisDto> historique;
    private UUID tourneeId;
    private UUID villeDepartId;
    private String villeDepartNom;
    private UUID villeArriveeId;
    private String villeArriveeNom;
    private UUID trajetId;
    private ModeRemise modeRemise;
    private String qrCode;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNumeroSuivi() {
        return numeroSuivi;
    }

    public void setNumeroSuivi(String numeroSuivi) {
        this.numeroSuivi = numeroSuivi;
    }

    public UUID getExpediteurId() {
        return expediteurId;
    }

    public void setExpediteurId(UUID expediteurId) {
        this.expediteurId = expediteurId;
    }

    public UserDTO getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(UserDTO expediteur) {
        this.expediteur = expediteur;
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

    public StatutColis getStatut() {
        return statut;
    }

    public void setStatut(StatutColis statut) {
        this.statut = statut;
    }

    public UUID getLivreurId() {
        return livreurId;
    }

    public void setLivreurId(UUID livreurId) {
        this.livreurId = livreurId;
    }

    public UserDTO getLivreur() {
        return livreur;
    }

    public void setLivreur(UserDTO livreur) {
        this.livreur = livreur;
    }

    public String getRemarques() {
        return remarques;
    }

    public void setRemarques(String remarques) {
        this.remarques = remarques;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(LocalDateTime dateLivraison) {
        this.dateLivraison = dateLivraison;
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

    public List<HistoriqueColisDto> getHistorique() {
        return historique;
    }

    public void setHistorique(List<HistoriqueColisDto> historique) {
        this.historique = historique;
    }

    public UUID getTourneeId() {
        return tourneeId;
    }

    public void setTourneeId(UUID tourneeId) {
        this.tourneeId = tourneeId;
    }

    public UUID getVilleDepartId() { return villeDepartId; }
    public void setVilleDepartId(UUID villeDepartId) { this.villeDepartId = villeDepartId; }

    public String getVilleDepartNom() { return villeDepartNom; }
    public void setVilleDepartNom(String villeDepartNom) { this.villeDepartNom = villeDepartNom; }

    public UUID getVilleArriveeId() { return villeArriveeId; }
    public void setVilleArriveeId(UUID villeArriveeId) { this.villeArriveeId = villeArriveeId; }

    public String getVilleArriveeNom() { return villeArriveeNom; }
    public void setVilleArriveeNom(String villeArriveeNom) { this.villeArriveeNom = villeArriveeNom; }

    public UUID getTrajetId() { return trajetId; }
    public void setTrajetId(UUID trajetId) { this.trajetId = trajetId; }

    public ModeRemise getModeRemise() { return modeRemise; }
    public void setModeRemise(ModeRemise modeRemise) { this.modeRemise = modeRemise; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
}
