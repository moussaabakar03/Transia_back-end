package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.VehiculeDto;

public interface VehiculeServiceInterface {
    VehiculeDto create(VehiculeDto vehiculeDto);
    VehiculeDto update(VehiculeDto vehiculeDto, UUID id);
    void delete(UUID id);
    VehiculeDto getVehicule(UUID id);
    List<VehiculeDto> listeVehicule();
    List<VehiculeDto> ListevehiculeDisponible();
    List<VehiculeDto> getDisponiblesByVille(UUID villeId);
}
