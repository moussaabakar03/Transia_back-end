package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.SuiviTrajetDto;

public interface SuiviTrajetServiceInterface {
    SuiviTrajetDto demarrerSuivi(UUID trajetId);
    SuiviTrajetDto mettreAJourStatut(Long suiviId, String nouveauStatut);
    SuiviTrajetDto getSuiviParTrajet(UUID trajetId);
}