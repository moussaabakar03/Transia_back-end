package com.ipnet.services.interfaces;

import java.util.List;

import com.ipnet.dto.VilleDto;

public interface VilleServiceInterface {
    VilleDto create(VilleDto villeDto);
    VilleDto update(VilleDto villeDto, Long id);
    void delete(Long id);
    VilleDto getVille(Long id);
    List<VilleDto> listeVille();
}