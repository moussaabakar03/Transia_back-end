package com.ipnet.services.implement;

import com.ipnet.dto.*;
import com.ipnet.entity.*;
import com.ipnet.enums.StatutTrajet;
import com.ipnet.enums.StatutVehicule;
import com.ipnet.mappers.TrajetMapper;
import com.ipnet.repository.*;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.TrajetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrajetServiceImpl implements TrajetService {

    private final TrajetRepository trajetRepository;
    private final VilleRepository villeRepository;
    private final VehiculeRepository vehiculeRepository;
    private final UserRepository userRepository;   // ← injection
    private final TrajetMapper trajetMapper;

    public TrajetServiceImpl(TrajetRepository trajetRepository,
                             VilleRepository villeRepository,
                             VehiculeRepository vehiculeRepository,
                             UserRepository userRepository,
                             TrajetMapper trajetMapper) {
        this.trajetRepository = trajetRepository;
        this.villeRepository = villeRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.userRepository = userRepository;
        this.trajetMapper = trajetMapper;
    }

    @Override
    public TrajetResponseDto creerTrajet(TrajetRequestDto request) {
        VilleEntity depart = villeRepository.findById(request.getVilleDepartId())
                .orElseThrow(() -> new RuntimeException("Départ non trouvé"));
        VilleEntity arrivee = villeRepository.findById(request.getVilleArriveeId())
                .orElseThrow(() -> new RuntimeException("Arrivée non trouvée"));
        VehiculeEntity vehicule = vehiculeRepository.findById(request.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

        User chauffeur = null;
        if (request.getChauffeurId() != null) {
            chauffeur = userRepository.findById(request.getChauffeurId())
                    .orElseThrow(() -> new RuntimeException("Chauffeur non trouvé"));
        }

        TrajetEntity entity = new TrajetEntity();
        entity.setVilleDepart(depart);
        entity.setVilleArrivee(arrivee);
        entity.setVehicule(vehicule);
        entity.setChauffeur(chauffeur);
        entity.setDistance(request.getDistance());
        entity.setDureeEstimee(request.getDureeEstimee());
        entity.setTarif(request.getTarif());
        entity.setDateDepart(request.getDateDepart());
        entity.setHeureDepart(request.getHeureDepart());
        entity.setStatut(request.getStatut() != null ? request.getStatut() : StatutTrajet.PROGRAMME);

        // Mettre le véhicule en service
        vehicule.setStatut(StatutVehicule.En_Service);
        vehiculeRepository.save(vehicule);

        return trajetMapper.toResponse(trajetRepository.save(entity));
    }

    @Override
    public List<TrajetResponseDto> listerTousLesTrajets() {
        return trajetRepository.findAll()
                .stream()
                .map(trajetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TrajetResponseDto obtenirTrajet(UUID id) {
        return trajetRepository.findById(id)
                .map(trajetMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Trajet introuvable"));
    }

    // Supprimer un trajet (méthode existante)
    @Override
    public void supprimerTrajet(UUID id) {
        trajetRepository.deleteById(id);
    }
}