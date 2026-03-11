package com.ipnet.services.interfaces;

import java.util.List;
import com.ipnet.dto.VehiculeDto;


public interface VehiculeServiceInterface {
	public VehiculeDto create(VehiculeDto vehiculeDto);
	public VehiculeDto update(VehiculeDto vehiculeDto, Long id);
	public void delete(Long id);
	public VehiculeDto getVehicule(Long id);
	public List<VehiculeDto> listeVehicule();
	
}
