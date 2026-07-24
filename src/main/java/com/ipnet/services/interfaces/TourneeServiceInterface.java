package com.ipnet.services.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.ipnet.dto.TourneeDto;
import com.ipnet.dto.TourneeRequestDto;

public interface TourneeServiceInterface {
    TourneeDto create(TourneeRequestDto dto);
    TourneeDto getById(UUID id);
    List<TourneeDto> listTournees();
    List<TourneeDto> filterTournees(LocalDate date, UUID livreurId, String zone);
    TourneeDto updatePartial(UUID id, TourneeRequestDto dto);
    void delete(UUID id);
    TourneeDto addDemandeToTournee(UUID tourneeId, UUID demandeId);
    TourneeDto removeDemandeFromTournee(UUID tourneeId, UUID demandeId);
}
