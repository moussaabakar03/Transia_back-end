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
        e.setKilometrage(dto.getKilometrage());
        // villeBase, villeActuelle et agence sont résolues dans le service (nécessite les repositories associés)
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
        dto.setKilometrage(e.getKilometrage());

        if (e.getVilleBase() != null) {
            dto.setVilleBaseId(e.getVilleBase().getId());
            dto.setVilleBaseNom(e.getVilleBase().getNomVille());
        }
        if (e.getVilleActuelle() != null) {
            dto.setVilleActuelleId(e.getVilleActuelle().getId());
            dto.setVilleActuelleNom(e.getVilleActuelle().getNomVille());
        }
        if (e.getAgence() != null) {
            dto.setAgenceId(e.getAgence().getId());
            dto.setAgenceNom(e.getAgence().getNom());
        }
        return dto;
    }
}
