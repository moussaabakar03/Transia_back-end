package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.DemandeCollecteDto;
import com.ipnet.dto.DemandeCollecteRequestDto;

public interface DemandeCollecteServiceInterface {
    DemandeCollecteDto creerDemande(DemandeCollecteRequestDto dto);
    DemandeCollecteDto assignerLivreur(UUID demandeId, UUID livreurId);
    DemandeCollecteDto collecterColis(UUID demandeId, UUID colisId);
    DemandeCollecteDto annulerDemande(UUID demandeId);
    List<DemandeCollecteDto> listerDemandes(UUID agenceId);
    List<DemandeCollecteDto> listerDemandesLivreur(UUID livreurId);

    // Absent de la spec initiale : nécessaire pour que le client mobile suive ses propres demandes.
    List<DemandeCollecteDto> listerMesDemandes();
}
