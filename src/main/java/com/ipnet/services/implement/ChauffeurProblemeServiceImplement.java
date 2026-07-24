package com.ipnet.services.implement;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipnet.dto.ChauffeurProblemeDto;
import com.ipnet.entity.ChauffeurProblemeEntity;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.repository.ChauffeurProblemeRepository;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.ChauffeurProblemeServiceInterface;

@Service
public class ChauffeurProblemeServiceImplement implements ChauffeurProblemeServiceInterface {

    @Autowired
    private ChauffeurProblemeRepository repository;

    @Autowired
    private TrajetRepository trajetRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ChauffeurProblemeDto save(ChauffeurProblemeDto dto) {
        TrajetEntity trajet = trajetRepository.findById(dto.getTrajetId())
                .orElseThrow(() -> new RuntimeException("Trajet introuvable."));

        User chauffeur = userRepository.findById(dto.getChauffeurId())
                .orElseThrow(() -> new RuntimeException("Chauffeur introuvable."));

        ChauffeurProblemeEntity entity = new ChauffeurProblemeEntity();
        entity.setTrajet(trajet);
        entity.setChauffeur(chauffeur);
        entity.setTypeProbleme(dto.getTypeProbleme());
        entity.setDescription(dto.getDescription());
        entity.setStatut(dto.getStatut() == null || dto.getStatut().isBlank() ? "EN_ATTENTE" : dto.getStatut());
        entity.setCreerPar(chauffeur.getNom());

        ChauffeurProblemeEntity saved = repository.save(entity);

        return toDto(saved);
    }

    @Override
    public List<ChauffeurProblemeDto> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChauffeurProblemeDto> getByTrajet(UUID trajetId) {
        return repository.findByTrajet_Id(trajetId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChauffeurProblemeDto> getByChauffeur(Long chauffeurId) {
        return repository.findByChauffeur_Id(chauffeurId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ChauffeurProblemeDto toDto(ChauffeurProblemeEntity entity) {
        ChauffeurProblemeDto dto = new ChauffeurProblemeDto();
        dto.setId(entity.getId());
        dto.setTrajetId(entity.getTrajet().getId());
        dto.setChauffeurId(entity.getChauffeur().getId());
        dto.setTypeProbleme(entity.getTypeProbleme());
        dto.setDescription(entity.getDescription());
        dto.setStatut(entity.getStatut());
        dto.setDateCreation(entity.getDateCreation());

        String villeDepart = entity.getTrajet().getVilleDepart() != null
                ? entity.getTrajet().getVilleDepart().getNomVille()
                : "";
        String villeArrivee = entity.getTrajet().getVilleArrivee() != null
                ? entity.getTrajet().getVilleArrivee().getNomVille()
                : "";

        dto.setTrajetLabel(villeDepart + " → " + villeArrivee);
        dto.setChauffeurNom(entity.getChauffeur().getNom());

        return dto;
    }
}