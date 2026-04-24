package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.VilleDto;

public interface VilleServiceInterface {
    VilleDto create(VilleDto villeDto);
    VilleDto update(VilleDto villeDto, UUID id);
    void delete(UUID id);
    VilleDto getVille(UUID id);
    List<VilleDto> listeVille();
}
