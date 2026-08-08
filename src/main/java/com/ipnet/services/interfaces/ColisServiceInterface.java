package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.ColisDto;
import com.ipnet.dto.ColisRequestDto;
import com.ipnet.dto.ColisStatutDto;
import com.ipnet.dto.HistoriqueColisDto;
import com.ipnet.enums.StatutColis;
import com.ipnet.enums.TranchePoids;

public interface ColisServiceInterface {
    ColisDto enregistrerColis(ColisRequestDto dto);
    ColisDto confirmerPeseeAjusterPrix(UUID colisId, Double poidsReel, TranchePoids trancheReelle);
    ColisDto chargerColisInTrajet(UUID colisId, UUID trajetId);
    ColisDto receptionnerColis(UUID colisId);
    ColisDto affecterLivreur(UUID colisId, UUID livreurId);
    ColisDto demarrerLivraison(UUID colisId);
    ColisDto confirmerLivraison(UUID colisId);
    ColisStatutDto getStatutColis(String numeroSuivi);
    List<ColisDto> listerColisParAgence(UUID agenceId);
    List<ColisDto> listerColisParStatut(StatutColis statut);

    // Absent de la spec initiale (endpoints listés = agent/admin uniquement) mais nécessaire :
    // sans ça, le client mobile n'a aucun moyen de lister les colis qu'il a lui-même envoyés.
    List<ColisDto> listerMesColis();

    // Idem côté livreur : sans ça, aucun moyen de lister ses livraisons en cours.
    List<ColisDto> listerMesLivraisons();

    // Conservés de l'existant : pas dans la spec mais toujours utiles (détail, audit, annulation)
    ColisDto getById(UUID id);
    List<HistoriqueColisDto> getHistorique(UUID colisId);
    void annulerColis(UUID colisId);
}
