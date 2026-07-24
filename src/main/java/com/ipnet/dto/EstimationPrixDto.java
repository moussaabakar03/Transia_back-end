package com.ipnet.dto;

public class EstimationPrixDto {
    private Double prixExpedition;
    private Double fraisCollecte;
    private Double fraisLivraison;
    private Double totalEstime;

    public EstimationPrixDto() {}

    public EstimationPrixDto(Double prixExpedition, Double fraisCollecte, Double fraisLivraison) {
        this.prixExpedition = prixExpedition;
        this.fraisCollecte = fraisCollecte;
        this.fraisLivraison = fraisLivraison;
        this.totalEstime = prixExpedition + fraisCollecte + fraisLivraison;
    }

    public Double getPrixExpedition() { return prixExpedition; }
    public void setPrixExpedition(Double prixExpedition) { this.prixExpedition = prixExpedition; }

    public Double getFraisCollecte() { return fraisCollecte; }
    public void setFraisCollecte(Double fraisCollecte) { this.fraisCollecte = fraisCollecte; }

    public Double getFraisLivraison() { return fraisLivraison; }
    public void setFraisLivraison(Double fraisLivraison) { this.fraisLivraison = fraisLivraison; }

    public Double getTotalEstime() { return totalEstime; }
    public void setTotalEstime(Double totalEstime) { this.totalEstime = totalEstime; }
}
