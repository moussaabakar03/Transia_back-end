package com.ipnet.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ipnet.enums.ModeRemise;
import com.ipnet.enums.StatutColis;
import com.ipnet.enums.StatutPaiementColis;
import com.ipnet.enums.TranchePoids;

public class ColisDto {
    private UUID id;
    private String numeroSuivi;
    private String codeRetrait;
    private String lienSuivi;
    private String description;
    private TranchePoids tranchePoids;
    private Double poidsReel;
    private String dimensions;
    private StatutColis statut;
    private StatutPaiementColis statutPaiement;
    private ModeRemise modeRemise;
    private String expediteurNom;
    private String expediteurTelephone;
    private String destinataireNom;
    private String destinataireTelephone;
    private String destinataireAdresse;
    private Double prixEstime;
    private Double prixFinal;
    private Double fraisCollecte;
    private Double fraisLivraison;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLivraison;
    private UUID agenceDepartId;
    private String agenceDepartNom;
    private UUID agenceArriveeId;
    private String agenceArriveeNom;
    private UUID trajetId;
    private String trajetInfo;
    private UUID agentEnregistreurId;
    private String agentEnregistreurNom;
    private UUID livreurId;
    private String livreurNom;
    private String qrCode;
    private String adresseCollecte;
    private Double latitudeCollecte;
    private Double longitudeCollecte;
    private List<HistoriqueColisDto> historique;

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

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }

    public UUID getAgenceDepartId() { return agenceDepartId; }
    public void setAgenceDepartId(UUID agenceDepartId) { this.agenceDepartId = agenceDepartId; }

    public String getAgenceDepartNom() { return agenceDepartNom; }
    public void setAgenceDepartNom(String agenceDepartNom) { this.agenceDepartNom = agenceDepartNom; }

    public UUID getAgenceArriveeId() { return agenceArriveeId; }
    public void setAgenceArriveeId(UUID agenceArriveeId) { this.agenceArriveeId = agenceArriveeId; }

    public String getAgenceArriveeNom() { return agenceArriveeNom; }
    public void setAgenceArriveeNom(String agenceArriveeNom) { this.agenceArriveeNom = agenceArriveeNom; }

    public UUID getTrajetId() { return trajetId; }
    public void setTrajetId(UUID trajetId) { this.trajetId = trajetId; }

    public String getTrajetInfo() { return trajetInfo; }
    public void setTrajetInfo(String trajetInfo) { this.trajetInfo = trajetInfo; }

    public UUID getAgentEnregistreurId() { return agentEnregistreurId; }
    public void setAgentEnregistreurId(UUID agentEnregistreurId) { this.agentEnregistreurId = agentEnregistreurId; }

    public String getAgentEnregistreurNom() { return agentEnregistreurNom; }
    public void setAgentEnregistreurNom(String agentEnregistreurNom) { this.agentEnregistreurNom = agentEnregistreurNom; }

    public UUID getLivreurId() { return livreurId; }
    public void setLivreurId(UUID livreurId) { this.livreurId = livreurId; }

    public String getLivreurNom() { return livreurNom; }
    public void setLivreurNom(String livreurNom) { this.livreurNom = livreurNom; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public String getAdresseCollecte() { return adresseCollecte; }
    public void setAdresseCollecte(String adresseCollecte) { this.adresseCollecte = adresseCollecte; }

    public Double getLatitudeCollecte() { return latitudeCollecte; }
    public void setLatitudeCollecte(Double latitudeCollecte) { this.latitudeCollecte = latitudeCollecte; }

    public Double getLongitudeCollecte() { return longitudeCollecte; }
    public void setLongitudeCollecte(Double longitudeCollecte) { this.longitudeCollecte = longitudeCollecte; }

    public List<HistoriqueColisDto> getHistorique() { return historique; }
    public void setHistorique(List<HistoriqueColisDto> historique) { this.historique = historique; }
}
