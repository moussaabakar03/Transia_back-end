package com.ipnet.services.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipnet.dto.VilleDto;
import com.ipnet.entity.VilleEntity;
import com.ipnet.repository.VilleRepository;
import com.ipnet.services.interfaces.VilleServiceInterface;

@Service
public class VilleServiceImplement implements VilleServiceInterface {

    private final VilleRepository villeRepository;

    public VilleServiceImplement(VilleRepository villeRepository) {
        this.villeRepository = villeRepository;
    }

    @Override
    public VilleDto create(VilleDto villeDto) {
        // Correction : Utilisation des setters au lieu d'un constructeur à 3 arguments
        VilleEntity ville = new VilleEntity();
        ville.setNomVille(villeDto.getNomVille());
        ville.setRegion(villeDto.getRegion());
        
        VilleEntity saved = villeRepository.save(ville);
        
        // On retourne le DTO avec l'ID généré par la base de données
        return new VilleDto(saved.getId(), saved.getNomVille(), saved.getRegion());
    }

    @Override
    public VilleDto update(VilleDto villeDto, UUID id) {
        VilleEntity ville = villeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ville non trouvée avec l'id : " + id));
        
        ville.setNomVille(villeDto.getNomVille());
        ville.setRegion(villeDto.getRegion());
        
        VilleEntity updated = villeRepository.save(ville);
        return new VilleDto(updated.getId(), updated.getNomVille(), updated.getRegion());
    }

    @Override
    public void delete(UUID id) {
        if (!villeRepository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer : Ville non trouvée");
        }
        villeRepository.deleteById(id);
    }

    @Override
    public VilleDto getVille(UUID id) {
        VilleEntity ville = villeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ville non trouvée"));
        return new VilleDto(ville.getId(), ville.getNomVille(), ville.getRegion());
    }

    @Override
    public List<VilleDto> listeVille() {
        // Version plus moderne avec Java Stream pour transformer la liste
        return villeRepository.findAll().stream()
            .map(v -> new VilleDto(v.getId(), v.getNomVille(), v.getRegion()))
            .collect(Collectors.toList());
    }
}
