package com.ipnet.services.implement;

import com.ipnet.dto.AgenceDto;
import com.ipnet.entity.AgenceEntity;
import com.ipnet.entity.VilleEntity;
import com.ipnet.repository.AgenceRepository;
import com.ipnet.repository.VilleRepository;
import com.ipnet.security.exception.ResourceNotFoundException;
import com.ipnet.services.interfaces.AgenceServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AgenceServiceImpl implements AgenceServiceInterface {

    private final AgenceRepository agenceRepository;
    private final VilleRepository villeRepository;

    public AgenceServiceImpl(AgenceRepository agenceRepository, VilleRepository villeRepository) {
        this.agenceRepository = agenceRepository;
        this.villeRepository = villeRepository;
    }

    @Override
    public AgenceDto create(AgenceDto dto) {
        if (dto.getVilleId() == null) throw new ResourceNotFoundException("villeId est obligatoire");
        validerChampsObligatoires(dto);
        VilleEntity ville = villeRepository.findById(dto.getVilleId())
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable : " + dto.getVilleId()));

        AgenceEntity agence = new AgenceEntity();
        agence.setNom(dto.getNom());
        agence.setVille(ville);
        agence.setAdresse(dto.getAdresse());
        agence.setTelephone(dto.getTelephone());
        agence.setEmail(dto.getEmail());
        agence.setLatitude(dto.getLatitude());
        agence.setLongitude(dto.getLongitude());
        agence.setStatut(dto.getStatut() != null ? dto.getStatut() : Boolean.TRUE);
        if (dto.getPhotos() != null) {
            agence.setPhotos(dto.getPhotos());
        }

        return toDto(agenceRepository.save(agence));
    }

    @Override
    public AgenceDto update(UUID id, AgenceDto dto) {
        AgenceEntity agence = agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable : " + id));

        validerChampsObligatoires(dto);

        if (dto.getVilleId() != null) {
            VilleEntity ville = villeRepository.findById(dto.getVilleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable : " + dto.getVilleId()));
            agence.setVille(ville);
        }

        agence.setNom(dto.getNom());
        agence.setAdresse(dto.getAdresse());
        agence.setTelephone(dto.getTelephone());
        agence.setEmail(dto.getEmail());
        agence.setLatitude(dto.getLatitude());
        agence.setLongitude(dto.getLongitude());
        if (dto.getPhotos() != null) {
            agence.setPhotos(dto.getPhotos());
        }
        // Le statut se change volontairement via updateStatut() dédié, pas ici : évite qu'une simple
        // modification d'adresse désactive une agence par un champ oublié dans le formulaire.

        return toDto(agenceRepository.save(agence));
    }

    @Override
    public AgenceDto updateStatut(UUID id, boolean statut) {
        AgenceEntity agence = agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable : " + id));
        agence.setStatut(statut);
        return toDto(agenceRepository.save(agence));
    }

    /**
     * nom/adresse/telephone/latitude/longitude sont obligatoires à la création comme à la modification,
     * mais validés en application plutôt qu'en contrainte SQL NOT NULL (des lignes existantes en base
     * pourraient déjà avoir ces champs vides, une contrainte stricte ferait échouer le démarrage).
     */
    private void validerChampsObligatoires(AgenceDto dto) {
        if (dto.getNom() == null || dto.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de l'agence est obligatoire");
        }
        if (dto.getAdresse() == null || dto.getAdresse().isBlank()) {
            throw new IllegalArgumentException("L'adresse de l'agence est obligatoire");
        }
        if (dto.getTelephone() == null || dto.getTelephone().isBlank()) {
            throw new IllegalArgumentException("Le téléphone de l'agence est obligatoire");
        }
        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            throw new IllegalArgumentException("Les coordonnées GPS (latitude/longitude) de l'agence sont obligatoires");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AgenceDto getById(UUID id) {
        return agenceRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgenceDto> getAll() {
        return agenceRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgenceDto> getByVille(UUID villeId) {
        return agenceRepository.findByVille_Id(villeId).stream().map(this::toDto).toList();
    }

    @Override
    public void delete(UUID id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence introuvable : " + id);
        }
        agenceRepository.deleteById(id);
    }

    private AgenceDto toDto(AgenceEntity e) {
        AgenceDto dto = new AgenceDto();
        dto.setId(e.getId());
        dto.setNom(e.getNom());
        dto.setAdresse(e.getAdresse());
        dto.setTelephone(e.getTelephone());
        dto.setEmail(e.getEmail());
        dto.setLatitude(e.getLatitude());
        dto.setLongitude(e.getLongitude());
        dto.setStatut(e.getStatut());
        dto.setPhotos(e.getPhotos());
        if (e.getVille() != null) {
            dto.setVilleId(e.getVille().getId());
            dto.setVilleNom(e.getVille().getNomVille());
        }
        return dto;
    }
}
