package com.ipnet.dto;

import java.util.UUID;

import com.ipnet.enums.ModeRemise;
import com.ipnet.enums.TranchePoids;

public class ColisRequestDto {
    private String description;
    private TranchePoids tranchePoids;
    private String dimensions;
    private ModeRemise modeRemise;
    private String expediteurNom;
    private String expediteurTelephone;
    private String destinataireNom;
    private String destinataireTelephone;
    private String destinataireAdresse;
    private UUID agenceDepartId;
    private UUID agenceArriveeId;
    private boolean collecteDomicile;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TranchePoids getTranchePoids() { return tranchePoids; }
    public void setTranchePoids(TranchePoids tranchePoids) { this.tranchePoids = tranchePoids; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

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

    public UUID getAgenceDepartId() { return agenceDepartId; }
    public void setAgenceDepartId(UUID agenceDepartId) { this.agenceDepartId = agenceDepartId; }

    public UUID getAgenceArriveeId() { return agenceArriveeId; }
    public void setAgenceArriveeId(UUID agenceArriveeId) { this.agenceArriveeId = agenceArriveeId; }

    public boolean isCollecteDomicile() { return collecteDomicile; }
    public void setCollecteDomicile(boolean collecteDomicile) { this.collecteDomicile = collecteDomicile; }
}
