package com.ipnet.services.interfaces;

import com.ipnet.dto.AgenceDto;

import java.util.List;
import java.util.UUID;

public interface AgenceServiceInterface {
    AgenceDto create(AgenceDto dto);
    AgenceDto update(UUID id, AgenceDto dto);
    AgenceDto getById(UUID id);
    List<AgenceDto> getAll();
    List<AgenceDto> getByVille(UUID villeId);
    void delete(UUID id);
}
