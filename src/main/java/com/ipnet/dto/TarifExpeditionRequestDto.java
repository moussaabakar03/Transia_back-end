package com.ipnet.dto;

import java.util.UUID;

import com.ipnet.enums.TranchePoids;

public class TarifExpeditionRequestDto {
    private UUID villeDepartId;
    private UUID villeArriveeId;
    private TranchePoids tranchePoids;
    private Double tarif;

    public UUID getVilleDepartId() { return villeDepartId; }
    public void setVilleDepartId(UUID villeDepartId) { this.villeDepartId = villeDepartId; }

    public UUID getVilleArriveeId() { return villeArriveeId; }
    public void setVilleArriveeId(UUID villeArriveeId) { this.villeArriveeId = villeArriveeId; }

    public TranchePoids getTranchePoids() { return tranchePoids; }
    public void setTranchePoids(TranchePoids tranchePoids) { this.tranchePoids = tranchePoids; }

    public Double getTarif() { return tarif; }
    public void setTarif(Double tarif) { this.tarif = tarif; }
}
