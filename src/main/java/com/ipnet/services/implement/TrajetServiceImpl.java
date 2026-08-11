package com.ipnet.services.implement;

import com.ipnet.dto.*;
import com.ipnet.entity.*;
import com.ipnet.enums.StatutTrajet;
import com.ipnet.enums.StatutVehicule;
import com.ipnet.exception.ChauffeurIndisponibleException;
import com.ipnet.exception.TrajetIntrouvableException;
import com.ipnet.exception.VehiculeIndisponibleException;
import com.ipnet.exception.VehiculeIntrouvableException;
import com.ipnet.mappers.TrajetMapper;
import com.ipnet.repository.*;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.TrajetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrajetServiceImpl implements TrajetService {

    private static final Set<StatutTrajet> STATUTS_OCCUPANTS = Set.of(StatutTrajet.PROGRAMME, StatutTrajet.EN_COURS);

    private final TrajetRepository trajetRepository;
    private final VilleRepository villeRepository;
    private final VehiculeRepository vehiculeRepository;
    private final UserRepository userRepository;
    private final AgenceRepository agenceRepository;
    private final TrajetMapper trajetMapper;

    public TrajetServiceImpl(TrajetRepository trajetRepository,
                             VilleRepository villeRepository,
                             VehiculeRepository vehiculeRepository,
                             UserRepository userRepository,
                             AgenceRepository agenceRepository,
                             TrajetMapper trajetMapper) {
        this.trajetRepository = trajetRepository;
        this.villeRepository = villeRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.userRepository = userRepository;
        this.agenceRepository = agenceRepository;
        this.trajetMapper = trajetMapper;
    }

    @Override
    public TrajetResponseDto creerTrajet(TrajetRequestDto request) {
        if (request.getDateDepart() != null && request.getDateDepart().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La date de départ ne peut pas être antérieure à la date d'aujourd'hui.");
        }

        AgenceEntity agenceDepart = resolveAgence(request.getAgenceDepartId() != null ? request.getAgenceDepartId() : request.getAgenceId());
        AgenceEntity agenceArrivee = resolveAgence(request.getAgenceArriveeId());

        VilleEntity depart = null;
        if (request.getVilleDepartId() != null) {
            depart = villeRepository.findById(request.getVilleDepartId()).orElse(null);
        }
        if (depart == null && agenceDepart != null && agenceDepart.getVille() != null) {
            depart = agenceDepart.getVille();
        }
        if (depart == null) {
            throw new RuntimeException("Départ non trouvé");
        }

        VilleEntity arrivee = null;
        if (request.getVilleArriveeId() != null) {
            arrivee = villeRepository.findById(request.getVilleArriveeId()).orElse(null);
        }
        if (arrivee == null && agenceArrivee != null && agenceArrivee.getVille() != null) {
            arrivee = agenceArrivee.getVille();
        }
        if (arrivee == null) {
            throw new RuntimeException("Arrivée non trouvée");
        }

        VehiculeEntity vehicule = vehiculeRepository.findById(request.getVehiculeId())
                .orElseThrow(VehiculeIntrouvableException::new);

        User chauffeur = resolveChauffeur(request.getChauffeurId());

        verifierVehiculeUtilisable(vehicule);
        verifierDisponibilite(vehicule.getId(), chauffeur, request.getDateDepart(), null);

        TrajetEntity entity = new TrajetEntity();
        entity.setVilleDepart(depart);
        entity.setVilleArrivee(arrivee);
        entity.setVehicule(vehicule);
        entity.setChauffeur(chauffeur);
        entity.setAgenceDepart(agenceDepart);
        entity.setAgenceArrivee(agenceArrivee);
        entity.setDistance(request.getDistance());
        entity.setDureeEstimee(request.getDureeEstimee());
        entity.setTarif(request.getTarif());
        entity.setDateDepart(request.getDateDepart());
        entity.setHeureDepart(request.getHeureDepart());
        entity.setStatut(request.getStatut() != null ? request.getStatut() : StatutTrajet.PROGRAMME);

        return trajetMapper.toResponse(trajetRepository.save(entity));
    }

    /**
     * Marque automatiquement les trajets PROGRAMME dont la date de départ est dépassée comme EXPIRE.
     */
    private void actualiserStatutsExpires() {
        List<TrajetEntity> programmes = trajetRepository.findByStatut(StatutTrajet.PROGRAMME);
        java.time.LocalDate aujourdhui = java.time.LocalDate.now();
        for (TrajetEntity t : programmes) {
            if (t.getDateDepart() != null && t.getDateDepart().isBefore(aujourdhui)) {
                t.setStatut(StatutTrajet.EXPIRE);
                trajetRepository.save(t);
            }
        }
    }

    /**
     * Un véhicule hors service ou en maintenance ne peut pas être programmé sur un trajet,
     * même si aucun autre trajet ne l'occupe à cette date.
     */
    private void verifierVehiculeUtilisable(VehiculeEntity vehicule) {
        if (vehicule.getStatut() == StatutVehicule.HORS_SERVICE
                || vehicule.getStatut() == StatutVehicule.EN_MAINTENANCE) {
            throw new VehiculeIndisponibleException();
        }
    }

    /**
     * Vérifie qu'aucun autre trajet PROGRAMME/EN_COURS n'occupe déjà ce véhicule ou ce chauffeur
     * à cette date. excludeTrajetId permet d'ignorer le trajet lui-même lors d'une modification.
     */
    private void verifierDisponibilite(UUID vehiculeId, User chauffeur, java.time.LocalDate date, UUID excludeTrajetId) {
        java.util.Optional<TrajetEntity> conflitVehicule = trajetRepository.findByVehicule_IdAndDateDepart(vehiculeId, date).stream()
                .filter(t -> excludeTrajetId == null || !t.getId().equals(excludeTrajetId))
                .filter(t -> STATUTS_OCCUPANTS.contains(t.getStatut()))
                .findFirst();
        if (conflitVehicule.isPresent()) {
            TrajetEntity c = conflitVehicule.get();
            String dept = c.getVilleDepart() != null ? c.getVilleDepart().getNomVille() : "...";
            String arr = c.getVilleArrivee() != null ? c.getVilleArrivee().getNomVille() : "...";
            throw new RuntimeException("Le véhicule sélectionné est déjà planifié le " + date + " sur le trajet " + dept + " → " + arr + " (" + c.getHeureDepart() + ")");
        }

        if (chauffeur != null) {
            java.util.Optional<TrajetEntity> conflitChauffeur = trajetRepository.findByChauffeur_IdAndDateDepart(chauffeur.getId(), date).stream()
                    .filter(t -> excludeTrajetId == null || !t.getId().equals(excludeTrajetId))
                    .filter(t -> STATUTS_OCCUPANTS.contains(t.getStatut()))
                    .findFirst();
            if (conflitChauffeur.isPresent()) {
                TrajetEntity c = conflitChauffeur.get();
                String dept = c.getVilleDepart() != null ? c.getVilleDepart().getNomVille() : "...";
                String arr = c.getVilleArrivee() != null ? c.getVilleArrivee().getNomVille() : "...";
                throw new RuntimeException("Le chauffeur sélectionné est déjà planifié le " + date + " sur le trajet " + dept + " → " + arr + " (" + c.getHeureDepart() + ")");
            }
        }
    }

    private User resolveChauffeur(UUID chauffeurPublicId) {
        if (chauffeurPublicId == null) return null;
        return userRepository.findByPublicId(chauffeurPublicId)
                .orElseThrow(() -> new RuntimeException("Chauffeur non trouvé"));
    }

    private AgenceEntity resolveAgence(UUID agenceId) {
        if (agenceId == null) return null;
        return agenceRepository.findById(agenceId)
                .orElseThrow(() -> new RuntimeException("Agence non trouvée"));
    }

    @Override
    public List<TrajetResponseDto> listerTousLesTrajets() {
        actualiserStatutsExpires();
        return trajetRepository.findAll()
                .stream()
                .map(trajetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TrajetResponseDto obtenirTrajet(UUID id) {
        actualiserStatutsExpires();
        return trajetRepository.findById(id)
                .map(trajetMapper::toResponse)
                .orElseThrow(TrajetIntrouvableException::new);
    }

    /**
     * "Suppression" = annulation logique : le trajet a probablement des réservations/billets/colis
     * rattachés (FK), donc pas de hard delete. On passe le statut à ANNULE et on libère le véhicule.
     */
    @Override
    public void supprimerTrajet(UUID id) {
        TrajetEntity entity = trajetRepository.findById(id)
                .orElseThrow(TrajetIntrouvableException::new);

        entity.setStatut(StatutTrajet.ANNULE);
        trajetRepository.save(entity);

        VehiculeEntity vehicule = entity.getVehicule();
        if (vehicule != null && vehicule.getStatut() == StatutVehicule.EN_ROUTE) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
            vehiculeRepository.save(vehicule);
        }

        // TODO(package Réservation & Billetterie) : notifier les passagers ayant une réservation sur ce trajet.
    }

    @Override
    public TrajetResponseDto modifierTrajet(UUID id, TrajetRequestDto request) {
        TrajetEntity entity = trajetRepository.findById(id)
                .orElseThrow(TrajetIntrouvableException::new);

        AgenceEntity agenceDepart = resolveAgence(request.getAgenceDepartId() != null ? request.getAgenceDepartId() : request.getAgenceId());
        AgenceEntity agenceArrivee = resolveAgence(request.getAgenceArriveeId());

        VilleEntity depart = null;
        if (request.getVilleDepartId() != null) {
            depart = villeRepository.findById(request.getVilleDepartId()).orElse(null);
        }
        if (depart == null && agenceDepart != null && agenceDepart.getVille() != null) {
            depart = agenceDepart.getVille();
        }
        if (depart == null) {
            depart = entity.getVilleDepart();
        }

        VilleEntity arrivee = null;
        if (request.getVilleArriveeId() != null) {
            arrivee = villeRepository.findById(request.getVilleArriveeId()).orElse(null);
        }
        if (arrivee == null && agenceArrivee != null && agenceArrivee.getVille() != null) {
            arrivee = agenceArrivee.getVille();
        }
        if (arrivee == null) {
            arrivee = entity.getVilleArrivee();
        }

        VehiculeEntity vehicule = vehiculeRepository.findById(request.getVehiculeId())
                .orElseThrow(VehiculeIntrouvableException::new);

        User chauffeur = resolveChauffeur(request.getChauffeurId());

        verifierVehiculeUtilisable(vehicule);
        verifierDisponibilite(vehicule.getId(), chauffeur, request.getDateDepart(), id);

        entity.setVilleDepart(depart);
        entity.setVilleArrivee(arrivee);
        entity.setVehicule(vehicule);
        entity.setChauffeur(chauffeur);
        entity.setAgenceDepart(agenceDepart != null ? agenceDepart : entity.getAgenceDepart());
        if (agenceArrivee != null) {
            entity.setAgenceArrivee(agenceArrivee);
        }
        entity.setDistance(request.getDistance());
        entity.setDureeEstimee(request.getDureeEstimee());
        entity.setTarif(request.getTarif());
        entity.setDateDepart(request.getDateDepart());
        entity.setHeureDepart(request.getHeureDepart());

        java.time.LocalDate aujourdhui = java.time.LocalDate.now();
        StatutTrajet statutDemande = request.getStatut();
        
        // Si le trajet était EXPIRE et qu'on le replanifie aujourd'hui ou dans le futur
        if (request.getDateDepart() != null && !request.getDateDepart().isBefore(aujourdhui)) {
            if (entity.getStatut() == StatutTrajet.EXPIRE && (statutDemande == null || statutDemande == StatutTrajet.EXPIRE)) {
                statutDemande = StatutTrajet.PROGRAMME;
            }
        } else if (request.getDateDepart() != null && request.getDateDepart().isBefore(aujourdhui)) {
            if (statutDemande == StatutTrajet.PROGRAMME || statutDemande == null) {
                statutDemande = StatutTrajet.EXPIRE;
            }
        }

        entity.setStatut(statutDemande != null ? statutDemande : StatutTrajet.PROGRAMME);

        return trajetMapper.toResponse(trajetRepository.save(entity));
    }
}
