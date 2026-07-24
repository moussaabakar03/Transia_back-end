package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.EstimationPrixDto;
import com.ipnet.dto.TarifExpeditionDto;
import com.ipnet.dto.TarifExpeditionRequestDto;
import com.ipnet.enums.ModeRemise;
import com.ipnet.enums.TranchePoids;

public interface TarifExpeditionServiceInterface {
    TarifExpeditionDto creerTarif(TarifExpeditionRequestDto dto);
    TarifExpeditionDto modifierTarif(UUID id, TarifExpeditionRequestDto dto);
    void supprimerTarif(UUID id);
    EstimationPrixDto estimerPrix(
            UUID villeDepartId, UUID villeArriveeId, TranchePoids tranche,
            ModeRemise modeRemise, boolean collecteDomicile);
    List<TarifExpeditionDto> listerTarifs();
    List<TarifExpeditionDto> listerTarifsParVilles(UUID departId, UUID arriveeId);
}
