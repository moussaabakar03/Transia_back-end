package com.ipnet.services.implement;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.dto.VehiculeDto;
import com.ipnet.entity.AgenceEntity;
import com.ipnet.entity.VehiculeEntity;
import com.ipnet.entity.VilleEntity;
import com.ipnet.enums.StatutVehicule;
import com.ipnet.mappers.VehiculeMappers;
import com.ipnet.repository.AgenceRepository;
import com.ipnet.repository.VehiculeRepository;
import com.ipnet.repository.VilleRepository;
import com.ipnet.security.exception.ResourceNotFoundException;
import com.ipnet.services.interfaces.VehiculeServiceInterface;

@Service
@Transactional
public class VehiculeServiceImplement implements VehiculeServiceInterface {

    private final VehiculeRepository vehiculeRepository;
    private final VehiculeMappers vehiculeMappers;
    private final VilleRepository villeRepository;
    private final AgenceRepository agenceRepository;

    public VehiculeServiceImplement(VehiculeRepository vehiculeRepository,
            VehiculeMappers vehiculeMappers, VilleRepository villeRepository,
            AgenceRepository agenceRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.vehiculeMappers = vehiculeMappers;
        this.villeRepository = villeRepository;
        this.agenceRepository = agenceRepository;
    }

    @Override
    public VehiculeDto create(VehiculeDto dto) {
        VehiculeEntity e = vehiculeMappers.toEntity(dto);
        resolveVilles(e, dto);
        resolveAgence(e, dto);
        return vehiculeMappers.toDto(vehiculeRepository.save(e));
    }

    @Override
    public VehiculeDto update(VehiculeDto dto, UUID id) {
        VehiculeEntity e = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable : " + id));

        e.setMarque(dto.getMarque());
        e.setModele(dto.getModele());
        e.setImmatriculation(dto.getImmatriculation());
        e.setCapacite(dto.getCapacite());
        e.setCapaciteSoute(dto.getCapaciteSoute());
        e.setStatut(dto.getStatut());
        e.setImage(dto.getImage());
        e.setKilometrage(dto.getKilometrage());
        resolveVilles(e, dto);
        resolveAgence(e, dto);

        return vehiculeMappers.toDto(vehiculeRepository.save(e));
    }

    @Override
    public void delete(UUID id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Véhicule introuvable : " + id);
        }
        vehiculeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeDto getVehicule(UUID id) {
        return vehiculeRepository.findById(id)
                .map(vehiculeMappers::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeDto> listeVehicule() {
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMappers::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeDto> ListevehiculeDisponible() {
        return vehiculeRepository.findByStatut(StatutVehicule.DISPONIBLE).stream()
                .map(vehiculeMappers::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeDto> getDisponiblesByVille(UUID villeId) {
        return vehiculeRepository
                .findByStatutAndVilleActuelle_Id(StatutVehicule.DISPONIBLE, villeId).stream()
                .map(vehiculeMappers::toDto)
                .collect(Collectors.toList());
    }

    private void resolveVilles(VehiculeEntity e, VehiculeDto dto) {
        if (dto.getVilleBaseId() != null) {
            VilleEntity base = villeRepository.findById(dto.getVilleBaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ville de base introuvable"));
            e.setVilleBase(base);
        }
        if (dto.getVilleActuelleId() != null) {
            VilleEntity actuelle = villeRepository.findById(dto.getVilleActuelleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ville actuelle introuvable"));
            e.setVilleActuelle(actuelle);
        }
    }

    private void resolveAgence(VehiculeEntity e, VehiculeDto dto) {
        if (dto.getAgenceId() != null) {
            AgenceEntity agence = agenceRepository.findById(dto.getAgenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable : " + dto.getAgenceId()));
            e.setAgence(agence);
        }
    }
}
