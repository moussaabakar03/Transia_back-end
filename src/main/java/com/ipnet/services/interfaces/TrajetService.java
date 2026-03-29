package com.ipnet.services.interfaces;

import com.ipnet.dto.TrajetRequestDto;
import com.ipnet.dto.TrajetResponseDto;

import java.util.List;

import com.ipnet.dto.TrajetRequestDto;
import com.ipnet.dto.TrajetResponseDto;

public interface TrajetService {
    TrajetResponseDto creerTrajet(TrajetRequestDto request);
    List<TrajetResponseDto> listerTousLesTrajets();
    TrajetResponseDto obtenirTrajet(Long id);
    void supprimerTrajet(Long id);
}