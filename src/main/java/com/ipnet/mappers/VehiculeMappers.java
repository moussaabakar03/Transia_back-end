package com.ipnet.mappers;

import org.springframework.stereotype.Component;

import com.ipnet.dto.VehiculeDto;
import com.ipnet.entity.VehiculeEntity;

@Component
public class VehiculeMappers {

    public VehiculeEntity toEntity(VehiculeDto dto) {
        VehiculeEntity e = new VehiculeEntity();
        e.setId(dto.getId());
        e.setMarque(dto.getMarque());
        e.setModele(dto.getModele());
        e.setImmatriculation(dto.getImmatriculation());
        e.setCapacite(dto.getCapacite());
        e.setCapaciteSoute(dto.getCapaciteSoute());
        e.setStatut(dto.getStatut());
        e.setImage(dto.getImage());
        // villeBase et villeActuelle sont résolues dans le service (nécessite VilleRepository)
        return e;
    }

    public VehiculeDto toDto(VehiculeEntity e) {
        VehiculeDto dto = new VehiculeDto();
        dto.setId(e.getId());
        dto.setMarque(e.getMarque());
        dto.setModele(e.getModele());
        dto.setImmatriculation(e.getImmatriculation());
        dto.setCapacite(e.getCapacite());
        dto.setCapaciteSoute(e.getCapaciteSoute());
        dto.setStatut(e.getStatut());
        dto.setImage(e.getImage());

        if (e.getVilleBase() != null) {
            dto.setVilleBaseId(e.getVilleBase().getId());
            dto.setVilleBaseNom(e.getVilleBase().getNomVille());
        }
        if (e.getVilleActuelle() != null) {
            dto.setVilleActuelleId(e.getVilleActuelle().getId());
            dto.setVilleActuelleNom(e.getVilleActuelle().getNomVille());
        }
        return dto;
    }
}
