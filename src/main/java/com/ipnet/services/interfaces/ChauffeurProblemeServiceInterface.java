package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.ChauffeurProblemeDto;

public interface ChauffeurProblemeServiceInterface {

    ChauffeurProblemeDto save(ChauffeurProblemeDto dto);

    List<ChauffeurProblemeDto> getAll();

    List<ChauffeurProblemeDto> getByTrajet(UUID trajetId);

    List<ChauffeurProblemeDto> getByChauffeur(Long chauffeurId);
}