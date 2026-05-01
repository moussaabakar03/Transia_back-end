package com.ipnet.mappers;

import com.ipnet.dto.PositionGpsDto;
import com.ipnet.entity.PositionGpsEntity;
import org.springframework.stereotype.Component;

@Component
public class PositionGpsMapper {

    public PositionGpsDto toDto(PositionGpsEntity entity) {
        if (entity == null) return null;
        PositionGpsDto dto = new PositionGpsDto();
        dto.setId(entity.getId());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setDateHeure(entity.getDateHeure());
        if (entity.getSuiviTrajet() != null) {
            dto.setSuiviTrajetId(entity.getSuiviTrajet().getId());
        }
        return dto;
    }

    public PositionGpsEntity toEntity(PositionGpsDto dto) {
        if (dto == null) return null;
        PositionGpsEntity entity = new PositionGpsEntity();
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setDateHeure(dto.getDateHeure());
        return entity;
    }
}