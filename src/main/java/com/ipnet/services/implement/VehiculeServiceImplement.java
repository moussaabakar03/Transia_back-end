package com.ipnet.services.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ipnet.dto.VehiculeDto;
import com.ipnet.entity.VehiculeEntity;
import com.ipnet.mappers.VehiculeMappers;
import com.ipnet.repository.VehiculeRepository;
import com.ipnet.services.interfaces.VehiculeServiceInterface;

@Service
public class VehiculeServiceImplement implements VehiculeServiceInterface{

	private VehiculeRepository vehiculeRepository;
	private VehiculeMappers vehiculeMappers;


	public VehiculeServiceImplement(VehiculeRepository vehiculeRepository, VehiculeMappers vehiculeMappers) {
		super();
		this.vehiculeRepository = vehiculeRepository;
		this.vehiculeMappers = vehiculeMappers;
	}

	@Override
	public VehiculeDto create(VehiculeDto vehiculeDto) {
		VehiculeEntity vehiculeEntity = vehiculeMappers.toEntity(vehiculeDto);		
		
		VehiculeEntity vehiculeSave = vehiculeRepository.save(vehiculeEntity);
		
		return vehiculeMappers.toDto(vehiculeSave);
	}

	/*@Override
		public VehiculeDto update(VehiculeDto vehiculeDto, Long id) {
		
		VehiculeEntity rechercheVehicule = vehiculeRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicule non trouvé"));
		VehiculeEntity vehicule = vehiculeMappers.toEntity(vehiculeDto);
		VehiculeEntity vehiculeSave = vehiculeRepository.save(vehicule);
		
		return vehiculeMappers.toDto(vehiculeSave);
	}*/

	@Override
	public VehiculeDto update(VehiculeDto vehiculeDto, UUID id) {
	    // 1. Vérifier que le véhicule existe
	    if (!vehiculeRepository.existsById(id)) {
	        throw new RuntimeException("Vehicule non trouvé avec l'id : " + id);
	    }
	    
	    // 2. Convertir le DTO en entité
	    VehiculeEntity vehicule = vehiculeMappers.toEntity(vehiculeDto);
	    
	    // 3. Forcer l'ID pour être sûr de faire un "Update" et non un "Create"
	    //vehicule.setId(id.longValue()); // Attention : ton entité utilise 'int id' et le DTO 'Long id'
	    vehicule.setId(id); // Attention : ton entité utilise 'int id' et le DTO 'Long id'

	    VehiculeEntity vehiculeSave = vehiculeRepository.save(vehicule);
	    return vehiculeMappers.toDto(vehiculeSave);
	}


	@Override
	public void delete(UUID id) {
		vehiculeRepository.deleteById(id);
	}

	@Override
	public VehiculeDto getVehicule(UUID id) {
		// TODO Auto-generated method stub
		VehiculeEntity rechercheVehicule = vehiculeRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicule non trouvé"));

		return vehiculeMappers.toDto(rechercheVehicule);
	}

	@Override
	public List<VehiculeDto> listeVehicule() {

		List<VehiculeEntity> vehicules = vehiculeRepository.findAll();
		
		ArrayList<VehiculeDto> vehiculesDto = new ArrayList<>();
		
		for(VehiculeEntity vehicule : vehicules)
			vehiculesDto.add(vehiculeMappers.toDto(vehicule));
		
		return vehiculesDto;
	}
	
	
	
	

}
