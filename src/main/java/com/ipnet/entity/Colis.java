package com.ipnet.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.ipnet.enums.ModeRemise;
import com.ipnet.enums.StatutColis;
import com.ipnet.enums.StatutPaiementColis;
import com.ipnet.enums.TranchePoids;
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

    @Column
    private String codeRetrait;

    @Column
    private String lienSuivi;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TranchePoids tranchePoids;

    @Column
    private Double poidsReel;

    @Column
    private String dimensions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutColis statut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiementColis statutPaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModeRemise modeRemise;

    @Column(nullable = false)
    private String expediteurNom;

    @Column(nullable = false)
    private String expediteurTelephone;

    @Column(nullable = false)
    private String destinataireNom;

    @Column(nullable = false)
    private String destinataireTelephone;

    @Column
    private String destinataireAdresse;

    @Column
    private Double prixEstime;

    @Column
    private Double prixFinal;

    @Column(nullable = false)
    private Double fraisCollecte;

    @Column(nullable = false)
    private Double fraisLivraison;

    @Column(name = "date_creation_colis", updatable = false)
    @CreationTimestamp
    private LocalDateTime dateCreationColis;

    @Column
    private LocalDateTime dateLivraison;

    @ManyToOne
    @JoinColumn(name = "agence_depart_id", nullable = false)
    private AgenceEntity agenceDepart;

    @ManyToOne
    @JoinColumn(name = "agence_arrivee_id", nullable = false)
    private AgenceEntity agenceArrivee;

    @ManyToOne
    @JoinColumn(name = "trajet_id", nullable = true)
    private TrajetEntity trajet;

    @ManyToOne
    @JoinColumn(name = "agent_enregistreur_id", nullable = false)
    private User agentEnregistreur;

    // Absent de la spec fournie, mais nécessaire : demarrerLivraison(colisId, livreurId) reçoit un
    // livreur qu'il faut bien persister quelque part pour que "mes livraisons" ait un sens côté livreur.
    @ManyToOne
    @JoinColumn(name = "livreur_id", nullable = true)
    private User livreur;

    @Column(name = "qr_code", unique = true)
    private String qrCode;

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL)
    private List<HistoriqueColis> historique = new ArrayList<>();

    public Colis() {
        this.statut = StatutColis.EN_ATTENTE_DEPOT;
        this.statutPaiement = StatutPaiementColis.EN_ATTENTE;
        this.fraisCollecte = 0.0;
        this.fraisLivraison = 0.0;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNumeroSuivi() { return numeroSuivi; }
    public void setNumeroSuivi(String numeroSuivi) { this.numeroSuivi = numeroSuivi; }

    public String getCodeRetrait() { return codeRetrait; }
    public void setCodeRetrait(String codeRetrait) { this.codeRetrait = codeRetrait; }

    public String getLienSuivi() { return lienSuivi; }
    public void setLienSuivi(String lienSuivi) { this.lienSuivi = lienSuivi; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TranchePoids getTranchePoids() { return tranchePoids; }
    public void setTranchePoids(TranchePoids tranchePoids) { this.tranchePoids = tranchePoids; }

    public Double getPoidsReel() { return poidsReel; }
    public void setPoidsReel(Double poidsReel) { this.poidsReel = poidsReel; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public StatutColis getStatut() { return statut; }
    public void setStatut(StatutColis statut) { this.statut = statut; }

    public StatutPaiementColis getStatutPaiement() { return statutPaiement; }
    public void setStatutPaiement(StatutPaiementColis statutPaiement) { this.statutPaiement = statutPaiement; }

    public ModeRemise getModeRemise() { return modeRemise; }
    public void setModeRemise(ModeRemise modeRemise) { this.modeRemise = modeRemise; }

    public String getExpediteurNom() { return expediteurNom; }
    public void setExpediteurNom(String expediteurNom) { this.expediteurNom = expediteurNom; }

    public String getExpediteurTelephone() { return expediteurTelephone; }
    public void setExpediteurTelephone(String expediteurTelephone) { this.expediteurTelephone = expediteurTelephone; }

    public String getDestinataireNom() { return destinataireNom; }
    public void setDestinataireNom(String destinataireNom) { this.destinataireNom = destinataireNom; }

    public String getDestinataireTelephone() { return destinataireTelephone; }
    public void setDestinataireTelephone(String destinataireTelephone) { this.destinataireTelephone = destinataireTelephone; }

    public String getDestinataireAdresse() { return destinataireAdresse; }
    public void setDestinataireAdresse(String destinataireAdresse) { this.destinataireAdresse = destinataireAdresse; }

    public Double getPrixEstime() { return prixEstime; }
    public void setPrixEstime(Double prixEstime) { this.prixEstime = prixEstime; }

    public Double getPrixFinal() { return prixFinal; }
    public void setPrixFinal(Double prixFinal) { this.prixFinal = prixFinal; }

    public Double getFraisCollecte() { return fraisCollecte; }
    public void setFraisCollecte(Double fraisCollecte) { this.fraisCollecte = fraisCollecte; }

    public Double getFraisLivraison() { return fraisLivraison; }
    public void setFraisLivraison(Double fraisLivraison) { this.fraisLivraison = fraisLivraison; }

    public LocalDateTime getDateCreationColis() { return dateCreationColis; }
    public void setDateCreationColis(LocalDateTime dateCreationColis) { this.dateCreationColis = dateCreationColis; }

    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }

    public AgenceEntity getAgenceDepart() { return agenceDepart; }
    public void setAgenceDepart(AgenceEntity agenceDepart) { this.agenceDepart = agenceDepart; }

    public AgenceEntity getAgenceArrivee() { return agenceArrivee; }
    public void setAgenceArrivee(AgenceEntity agenceArrivee) { this.agenceArrivee = agenceArrivee; }

    public TrajetEntity getTrajet() { return trajet; }
    public void setTrajet(TrajetEntity trajet) { this.trajet = trajet; }

    public User getAgentEnregistreur() { return agentEnregistreur; }
    public void setAgentEnregistreur(User agentEnregistreur) { this.agentEnregistreur = agentEnregistreur; }

    public User getLivreur() { return livreur; }
    public void setLivreur(User livreur) { this.livreur = livreur; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public List<HistoriqueColis> getHistorique() { return historique; }
    public void setHistorique(List<HistoriqueColis> historique) { this.historique = historique; }
}
