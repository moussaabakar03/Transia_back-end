package com.ipnet.mappers;

import com.ipnet.dto.TrajetDto;
import com.ipnet.entity.TrajetEntity;
import org.springframework.stereotype.Component;

@Component
public class TrajetMappers {

    public TrajetEntity toEntity(TrajetDto dto) {
        if (dto == null) return null;
        TrajetEntity entity = new TrajetEntity();
        entity.setId(dto.getId());
        entity.setVilleDepart(dto.getVilleDepart());
        entity.setVilleArrivee(dto.getVilleArrivee());
        entity.setDistance(dto.getDistance());
        entity.setDureeEstimee(dto.getDureeEstimee());
        entity.setTarif(dto.getTarif());
        entity.setDateDepart(dto.getDateDepart());
        entity.setHeureDepart(dto.getHeureDepart());
        entity.setStatut(dto.getStatut());
        return entity;
    }

    public TrajetDto toDto(TrajetEntity entity) {
        if (entity == null) return null;
        TrajetDto dto = new TrajetDto();
        dto.setId(entity.getId());
        dto.setVilleDepart(entity.getVilleDepart());
        dto.setVilleArrivee(entity.getVilleArrivee());
        dto.setDistance(entity.getDistance());
        dto.setDureeEstimee(entity.getDureeEstimee());
        dto.setTarif(entity.getTarif());
        dto.setDateDepart(entity.getDateDepart());
        dto.setHeureDepart(entity.getHeureDepart());
        dto.setStatut(entity.getStatut());
        if (entity.getVehicule() != null) {
            dto.setVehiculeId(entity.getVehicule().getId());
        }
        return dto;
    }
}