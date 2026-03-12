package com.ipnet.services.implement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ipnet.dto.VilleDto;
import com.ipnet.entity.VilleEntity;
import com.ipnet.repository.VilleRepository;
import com.ipnet.services.interfaces.VilleServiceInterface;

@Service
public class VilleServiceImplement implements VilleServiceInterface {

    private VilleRepository villeRepository;

    public VilleServiceImplement(VilleRepository villeRepository) {
        this.villeRepository = villeRepository;
    }

    @Override
    public VilleDto create(VilleDto villeDto) {
        VilleEntity ville = new VilleEntity(null, villeDto.getNomVille(), villeDto.getRegion());
        VilleEntity saved = villeRepository.save(ville);
        return new VilleDto(saved.getId(), saved.getNomVille(), saved.getRegion());
    }

    @Override
    public VilleDto update(VilleDto villeDto, Long id) {
        VilleEntity ville = villeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ville non trouvée"));
        ville.setNomVille(villeDto.getNomVille());
        ville.setRegion(villeDto.getRegion());
        VilleEntity updated = villeRepository.save(ville);
        return new VilleDto(updated.getId(), updated.getNomVille(), updated.getRegion());
    }

    @Override
    public void delete(Long id) {
        villeRepository.deleteById(id);
    }

    @Override
    public VilleDto getVille(Long id) {
        VilleEntity ville = villeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ville non trouvée"));
        return new VilleDto(ville.getId(), ville.getNomVille(), ville.getRegion());
    }

    @Override
    public List<VilleDto> listeVille() {
        List<VilleEntity> villes = villeRepository.findAll();
        List<VilleDto> dtos = new ArrayList<>();
        for (VilleEntity v : villes) {
            dtos.add(new VilleDto(v.getId(), v.getNomVille(), v.getRegion()));
        }
        return dtos;
    }
}