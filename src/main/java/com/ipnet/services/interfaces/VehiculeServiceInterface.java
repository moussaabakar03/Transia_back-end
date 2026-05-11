package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.VehiculeDto;


public interface VehiculeServiceInterface {
	public VehiculeDto create(VehiculeDto vehiculeDto);
	public VehiculeDto update(VehiculeDto vehiculeDto, UUID id);
	public void delete(UUID id);
	public VehiculeDto getVehicule(UUID id);
	public List<VehiculeDto> listeVehicule();
	public List<VehiculeDto> ListevehiculeDisponible();
	
}
