package com.ipnet.services.implement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ipnet.dto.TrajetDto;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.entity.VehiculeEntity;
import com.ipnet.mappers.TrajetMappers;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.repository.VehiculeRepository;
import com.ipnet.services.interfaces.TrajetServiceInterface;

@Service
public class TrajetServiceImplement implements TrajetServiceInterface {

    private TrajetRepository trajetRepository;
    private VehiculeRepository vehiculeRepository;
    private TrajetMappers trajetMappers;

    public TrajetServiceImplement(TrajetRepository trajetRepository, 
                                  VehiculeRepository vehiculeRepository, 
                                  TrajetMappers trajetMappers) {
        this.trajetRepository = trajetRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.trajetMappers = trajetMappers;
    }

    @Override
    public TrajetDto create(TrajetDto trajetDto) {
        
        TrajetEntity trajetEntity = trajetMappers.toEntity(trajetDto);
        
        
        if (trajetDto.getVehiculeId() != null) {
            VehiculeEntity vehicule = vehiculeRepository.findById(trajetDto.getVehiculeId())
                    .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
            trajetEntity.setVehicule(vehicule);
        }
        
        TrajetEntity trajetSave = trajetRepository.save(trajetEntity);
        return trajetMappers.toDto(trajetSave);
    }

    @Override
    public TrajetDto update(TrajetDto trajetDto, Long id) {
        if (!trajetRepository.existsById(id)) {
            throw new RuntimeException("Trajet non trouvé avec l'id : " + id);
        }
        
        TrajetEntity trajetEntity = trajetMappers.toEntity(trajetDto);
        trajetEntity.setId(id);
        
        if (trajetDto.getVehiculeId() != null) {
            VehiculeEntity vehicule = vehiculeRepository.findById(trajetDto.getVehiculeId())
                    .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
            trajetEntity.setVehicule(vehicule);
        }
        
        TrajetEntity trajetUpdated = trajetRepository.save(trajetEntity);
        return trajetMappers.toDto(trajetUpdated);
    }

    @Override
    public void delete(Long id) {
        if (!trajetRepository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer : Trajet inexistant");
        }
        trajetRepository.deleteById(id);
    }

    @Override
    public TrajetDto getTrajet(Long id) {
        TrajetEntity rechercheTrajet = trajetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));
        return trajetMappers.toDto(rechercheTrajet);
    }

    @Override
    public List<TrajetDto> listeTrajet() {
        List<TrajetEntity> trajets = trajetRepository.findAll();
        ArrayList<TrajetDto> trajetsDto = new ArrayList<>();
        
        for (TrajetEntity trajet : trajets) {
            trajetsDto.add(trajetMappers.toDto(trajet));
        }
        
        return trajetsDto;
    }
}