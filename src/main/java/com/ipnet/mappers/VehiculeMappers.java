package com.ipnet.mappers;

import org.springframework.stereotype.Component;

import com.ipnet.dto.VehiculeDto;
import com.ipnet.entity.VehiculeEntity;
import com.ipnet.enums.StatutVehicule;

@Component
public class VehiculeMappers {
	
	public VehiculeEntity toEntity(VehiculeDto vehiculeDto) {
		VehiculeEntity vehiculeEntity = new VehiculeEntity();
		
		vehiculeEntity.setId(vehiculeDto.getId());
		vehiculeEntity.setMarque(vehiculeDto.getMarque());
		vehiculeEntity.setImmatriculation(vehiculeDto.getImmatriculation());
		vehiculeEntity.setCapacite(vehiculeDto.getCapacite());
		vehiculeEntity.setStatut(vehiculeDto.getStatut());
		vehiculeEntity.setImage(vehiculeDto.getImage());
		
		return vehiculeEntity;
	}
	
	
	public VehiculeDto toDto(VehiculeEntity vehiculeEntity) {
		VehiculeDto vehiculeDto = new VehiculeDto();
		
		vehiculeDto.setId(vehiculeEntity.getId());
		vehiculeDto.setMarque(vehiculeEntity.getMarque());
		vehiculeDto.setImmatriculation(vehiculeEntity.getImmatriculation());
		vehiculeDto.setCapacite(vehiculeEntity.getCapacite());
		vehiculeDto.setStatut(vehiculeEntity.getStatut());
		vehiculeDto.setImage(vehiculeEntity.getImage());
		
		return vehiculeDto;
	}
}
