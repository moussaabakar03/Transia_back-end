package com.ipnet.dto;

import java.util.UUID;

import com.ipnet.enums.TranchePoids;

public class TarifExpeditionDto {
    private UUID id;
    private UUID villeDepartId;
    private String villeDepartNom;
    private UUID villeArriveeId;
    private String villeArriveeNom;
    private TranchePoids tranchePoids;
    private Double tarif;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getVilleDepartId() { return villeDepartId; }
    public void setVilleDepartId(UUID villeDepartId) { this.villeDepartId = villeDepartId; }

    public String getVilleDepartNom() { return villeDepartNom; }
    public void setVilleDepartNom(String villeDepartNom) { this.villeDepartNom = villeDepartNom; }

    public UUID getVilleArriveeId() { return villeArriveeId; }
    public void setVilleArriveeId(UUID villeArriveeId) { this.villeArriveeId = villeArriveeId; }

    public String getVilleArriveeNom() { return villeArriveeNom; }
    public void setVilleArriveeNom(String villeArriveeNom) { this.villeArriveeNom = villeArriveeNom; }

    public TranchePoids getTranchePoids() { return tranchePoids; }
    public void setTranchePoids(TranchePoids tranchePoids) { this.tranchePoids = tranchePoids; }

    public Double getTarif() { return tarif; }
    public void setTarif(Double tarif) { this.tarif = tarif; }
}
