package com.ipnet.services.implement;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ipnet.dto.VilleDto;
import com.ipnet.entity.VilleEntity;
import com.ipnet.repository.VilleRepository;
import com.ipnet.security.exception.ResourceNotFoundException;
import com.ipnet.services.interfaces.VilleServiceInterface;

@Service
public class VilleServiceImplement implements VilleServiceInterface {

    private final VilleRepository villeRepository;

    public VilleServiceImplement(VilleRepository villeRepository) {
        this.villeRepository = villeRepository;
    }

    @Override
    public VilleDto create(VilleDto villeDto) {
        VilleEntity ville = new VilleEntity();
        ville.setNomVille(villeDto.getNomVille());
        ville.setRegion(villeDto.getRegion());
        ville.setPays(villeDto.getPays() != null && !villeDto.getPays().isBlank() ? villeDto.getPays() : "Togo");

        return toDto(villeRepository.save(ville));
    }

    @Override
    public VilleDto update(VilleDto villeDto, UUID id) {
        VilleEntity ville = villeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable : " + id));

        ville.setNomVille(villeDto.getNomVille());
        ville.setRegion(villeDto.getRegion());
        if (villeDto.getPays() != null && !villeDto.getPays().isBlank()) {
            ville.setPays(villeDto.getPays());
        }

        return toDto(villeRepository.save(ville));
    }

    @Override
    public void delete(UUID id) {
        if (!villeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ville introuvable : " + id);
        }
        villeRepository.deleteById(id);
    }

    @Override
    public VilleDto getVille(UUID id) {
        return villeRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable : " + id));
    }

    @Override
    public List<VilleDto> listeVille() {
        return villeRepository.findAll().stream().map(this::toDto).toList();
    }

    private VilleDto toDto(VilleEntity ville) {
        VilleDto dto = new VilleDto();
        dto.setId(ville.getId());
        dto.setNomVille(ville.getNomVille());
        dto.setRegion(ville.getRegion());
        dto.setPays(ville.getPays());
        return dto;
    }
}
