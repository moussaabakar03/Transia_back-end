package com.ipnet.mappers;

import com.ipnet.dto.TrajetResponseDto;
import com.ipnet.dto.VehiculeDto;
import com.ipnet.dto.VilleDto;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.entity.VehiculeEntity;
import com.ipnet.entity.VilleEntity;

import org.springframework.stereotype.Component;

@Component
public class TrajetMapper {

    public TrajetResponseDto toResponse(TrajetEntity entity) {
        TrajetResponseDto dto = new TrajetResponseDto();
        dto.setId(entity.getId());
        dto.setVilleDepart(toVilleDto(entity.getVilleDepart()));
        dto.setVilleArrivee(toVilleDto(entity.getVilleArrivee()));
        dto.setVehicule(toVehiculeDto(entity.getVehicule()));
        dto.setDistance(entity.getDistance());
        dto.setDureeEstimee(entity.getDureeEstimee());
        dto.setTarif(entity.getTarif());
        dto.setDateDepart(entity.getDateDepart());
        dto.setHeureDepart(entity.getHeureDepart());
        dto.setStatut(entity.getStatut());

        // ChauffeurId
        if (entity.getChauffeur() != null) {
            dto.setChauffeurId(entity.getChauffeur().getId());
        }
        return dto;
    }

    private VilleDto toVilleDto(VilleEntity entity) {
        VilleDto dto = new VilleDto();
        dto.setId(entity.getId());
        dto.setNomVille(entity.getNomVille());
        dto.setRegion(entity.getRegion());
        return dto;
    }

    private VehiculeDto toVehiculeDto(VehiculeEntity entity) {
        VehiculeDto dto = new VehiculeDto();
        dto.setId(entity.getId());
        dto.setMarque(entity.getMarque());
        dto.setModele(entity.getModele());
        dto.setImmatriculation(entity.getImmatriculation());
        dto.setCapacite(entity.getCapacite());
        dto.setStatut(entity.getStatut());
        dto.setImage(entity.getImage());
        return dto;
    }
}