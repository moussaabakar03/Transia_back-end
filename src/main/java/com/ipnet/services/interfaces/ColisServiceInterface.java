package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.ColisDto;
import com.ipnet.dto.ColisRequestDto;
import com.ipnet.dto.HistoriqueColisDto;
import com.ipnet.enums.ModeDepot;
import com.ipnet.enums.StatutColis;

public interface ColisServiceInterface {
    ColisDto create(ColisRequestDto dto);
    ColisDto createDemandeEnlevement(ColisRequestDto dto);
    ColisDto getById(UUID id);
    ColisDto getByNumeroSuivi(String numeroSuivi);
    List<ColisDto> listColis();
    List<ColisDto> filterColis(StatutColis statut, UUID livreurId, UUID expediteurId, String search);
    List<ColisDto> findNearby(Double latitude, Double longitude, Double distanceKm);
    ColisDto updatePartial(UUID id, ColisRequestDto dto);
    ColisDto assignerLivreur(UUID colisId, UUID livreurId);
    ColisDto collecter(UUID colisId, String commentaire);
    ColisDto livrer(UUID colisId, String commentaire);
    List<HistoriqueColisDto> getHistorique(UUID colisId);
    void annulerColis(UUID colisId);
    String generateNumeroSuivi();
}
