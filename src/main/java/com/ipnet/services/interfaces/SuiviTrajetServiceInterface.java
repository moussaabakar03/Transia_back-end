package com.ipnet.services.interfaces;

import com.ipnet.dto.SuiviTrajetDto;
import com.ipnet.enums.StatutSuiviTrajet;

import java.util.UUID;

public interface SuiviTrajetServiceInterface {

    SuiviTrajetDto creerOuRecupererSuivi(UUID trajetId);

    SuiviTrajetDto demarrerSuivi(
            UUID trajetId,
            String username
    );

    SuiviTrajetDto mettreEnPause(
            Long suiviId,
            String username
    );

    SuiviTrajetDto reprendreSuivi(
            Long suiviId,
            String username
    );

    SuiviTrajetDto terminerSuivi(
            Long suiviId,
            String username
    );

    SuiviTrajetDto annulerSuivi(
            Long suiviId,
            String username
    );

    SuiviTrajetDto mettreAJourStatut(
            Long suiviId,
            StatutSuiviTrajet statut,
            String message,
            String username
    );

    SuiviTrajetDto getSuiviParTrajet(UUID trajetId);

    SuiviTrajetDto getSuiviParId(Long suiviId);
}