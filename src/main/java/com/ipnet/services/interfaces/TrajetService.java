package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.TrajetRequestDto;
import com.ipnet.dto.TrajetResponseDto;

public interface TrajetService {
    TrajetResponseDto creerTrajet(TrajetRequestDto request);
    List<TrajetResponseDto> listerTousLesTrajets();
    TrajetResponseDto obtenirTrajet(UUID id);
    void supprimerTrajet(UUID id);
}
