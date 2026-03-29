package com.ipnet.services.interfaces;

import com.ipnet.dto.SuiviTrajetDto;

public interface SuiviTrajetServiceInterface {
    SuiviTrajetDto demarrerSuivi(Long trajetId);
    SuiviTrajetDto mettreAJourStatut(Long suiviId, String nouveauStatut);
    SuiviTrajetDto getSuiviParTrajet(Long trajetId);
}