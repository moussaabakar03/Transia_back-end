package com.ipnet.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.dto.VehiculeDto;
import com.ipnet.services.interfaces.VehiculeServiceInterface;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/vehicule")
public class VehiculeController {

	private VehiculeServiceInterface vehiculeService;
	
	
	public VehiculeController(VehiculeServiceInterface vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

	@PostMapping
	public VehiculeDto create(@RequestBody VehiculeDto vehiculeDto) {
		return vehiculeService.create(vehiculeDto);
	}

	@PutMapping("/{id}")
	public VehiculeDto update(@RequestBody VehiculeDto vehiculeDto, @PathVariable UUID id) {
		return vehiculeService.update(vehiculeDto, id);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable UUID id) {
		vehiculeService.delete(id);
	}

	@GetMapping("/{id}") // L'id doit être entre accolades ici
	public VehiculeDto getVehicule(@PathVariable UUID id) { 
	    return vehiculeService.getVehicule(id);
	}
	
	@GetMapping
	public List<VehiculeDto> listeVehicule() {
		return vehiculeService.listeVehicule();
	}
	
	 @GetMapping("/disponible")
    public List<VehiculeDto> listeVehiculesDisponibles() {
        return vehiculeService.ListevehiculeDisponible();
    }

}
