package com.ipnet.services.implement;

import com.ipnet.dto.TrajetRequestDto;
import com.ipnet.dto.TrajetResponseDto;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.entity.VehiculeEntity;
import com.ipnet.entity.VilleEntity;
import com.ipnet.mappers.TrajetMapper;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.repository.VehiculeRepository;
import com.ipnet.repository.VilleRepository;
import com.ipnet.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrajetServiceImpl implements TrajetService {

    @Autowired private TrajetRepository trajetRepository;
    @Autowired private VilleRepository villeRepository;
    @Autowired private VehiculeRepository vehiculeRepository;
    @Autowired private TrajetMapper trajetMapper;

    @Override
    public TrajetResponseDto creerTrajet(TrajetRequestDto request) {
        VilleEntity depart = villeRepository.findById(request.getVilleDepartId())
                .orElseThrow(() -> new RuntimeException("Départ non trouvé"));
        VilleEntity arrivee = villeRepository.findById(request.getVilleArriveeId())
                .orElseThrow(() -> new RuntimeException("Arrivée non trouvée"));
        VehiculeEntity vehicule = vehiculeRepository.findById(request.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

        TrajetEntity entity = new TrajetEntity();
        entity.setVilleDepart(depart);
        entity.setVilleArrivee(arrivee);
        entity.setVehicule(vehicule);
        entity.setTarif(request.getTarif());
        entity.setDateDepart(request.getDateDepart());
        entity.setHeureDepart(request.getHeureDepart());
        entity.setStatut(request.getStatut());
        entity.setDistance(request.getDistance());
        entity.setDureeEstimee(request.getDureeEstimee());

        return trajetMapper.toResponse(trajetRepository.save(entity));
    }

    @Override
    public List<TrajetResponseDto> listerTousLesTrajets() {
        return trajetRepository.findAll().stream()
                .map(trajetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TrajetResponseDto obtenirTrajet(Long id) {
        return trajetRepository.findById(id)
                .map(trajetMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Trajet introuvable"));
    }

    @Override
    public void supprimerTrajet(Long id) {
        trajetRepository.deleteById(id);
    }
}