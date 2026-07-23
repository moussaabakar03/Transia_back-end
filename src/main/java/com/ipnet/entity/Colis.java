package com.ipnet.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.ipnet.enums.ModeDepot;
import com.ipnet.enums.ModeRemise;
import com.ipnet.enums.StatutColis;
import com.ipnet.security.model.User;
import com.ipnet.utils.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "colis")
public class Colis extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(unique = true, nullable = false)
    private String numeroSuivi;

    @ManyToOne
    // @JoinColumn(name = "expediteur_id", nullable = false)
    @JoinColumn(name = "expediteur_id", nullable = true)
    private User expediteur;

    @Column(nullable = false)
    private String nomDestinataire;

    @Column(nullable = false)
    private String adresseDestinataire;

    @Column(nullable = false)
    private String telephoneDestinataire;

    @Column(nullable = false)
    private Double poids;

    @Column(nullable = false)
    private Double longueur;

    @Column(nullable = false)
    private Double largeur;

    @Column(nullable = false)
    private Double hauteur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutColis statut;

    @ManyToOne
    @JoinColumn(name = "livreur_id", nullable = true)
    private User livreur;

    @Column(columnDefinition = "TEXT")
    private String remarques;

    @Column(name = "date_creation_colis", updatable = false)
    @CreationTimestamp
    private LocalDateTime dateCreationColis;
    
    @Column
    private LocalDateTime dateLivraison;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModeDepot modeDepot;

    @Column
    private String adresseCollecte;

    @Column
    private String telephoneCollecte;

    @Column
    private LocalDateTime dateHeureCollecteSouhaitee;

    @Column
    private Double latitudeDestinataire;

    @Column
    private Double longitudeDestinataire;

    @Column
    private Double latitudeCollecte;

    @Column
    private Double longitudeCollecte;

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL)
    private List<HistoriqueColis> historique = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "tournee_id", nullable = true)
    private Tournee tournee;

    @ManyToOne
    @JoinColumn(name = "ville_depart_id", nullable = true)
    private VilleEntity villeDepart;

    @ManyToOne
    @JoinColumn(name = "ville_arrivee_id", nullable = true)
    private VilleEntity villeArrivee;

    @ManyToOne
    @JoinColumn(name = "trajet_id", nullable = true)
    private TrajetEntity trajet;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_remise")
    private ModeRemise modeRemise;

    @Column(name = "qr_code", unique = true)
    private String qrCode;

    public Colis() {
        this.dateCreationColis = LocalDateTime.now();
        this.statut = StatutColis.EN_ATTENTE_COLLECTE;
    }

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

    public User getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(User expediteur) {
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

    public User getLivreur() {
        return livreur;
    }

    public void setLivreur(User livreur) {
        this.livreur = livreur;
    }

    public String getRemarques() {
        return remarques;
    }

    public void setRemarques(String remarques) {
        this.remarques = remarques;
    }

    public LocalDateTime getDateCreationColis() {
        return dateCreationColis;
    }
 
    public void setDateCreationColis(LocalDateTime dateCreation) {
        this.dateCreationColis = dateCreation;
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

    public List<HistoriqueColis> getHistorique() {
        return historique;
    }

    public void setHistorique(List<HistoriqueColis> historique) {
        this.historique = historique;
    }

    public Tournee getTournee() {
        return tournee;
    }

    public void setTournee(Tournee tournee) {
        this.tournee = tournee;
    }

    public VilleEntity getVilleDepart() { return villeDepart; }
    public void setVilleDepart(VilleEntity villeDepart) { this.villeDepart = villeDepart; }

    public VilleEntity getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(VilleEntity villeArrivee) { this.villeArrivee = villeArrivee; }

    public TrajetEntity getTrajet() { return trajet; }
    public void setTrajet(TrajetEntity trajet) { this.trajet = trajet; }

    public ModeRemise getModeRemise() { return modeRemise; }
    public void setModeRemise(ModeRemise modeRemise) { this.modeRemise = modeRemise; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
}
