package com.ipnet.mappers;

import com.ipnet.dto.PositionGpsDto;
import com.ipnet.dto.SuiviTrajetDto;
import com.ipnet.entity.PositionGpsEntity;
import com.ipnet.entity.SuiviTrajetEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SuiviTrajetMapper {

    @Autowired
    private PositionGpsMapper positionGpsMapper;


    public SuiviTrajetDto toDto(SuiviTrajetEntity entity) {
        if (entity == null) {
            return null;
        }

        SuiviTrajetDto dto = new SuiviTrajetDto();
        dto.setId(entity.getId());
        dto.setStatut(entity.getStatut());
        

        if (entity.getTrajet() != null) {
            dto.setTrajetId(entity.getTrajet().getId());
        }


        if (entity.getHistoriquePositions() != null) {
            List<PositionGpsDto> positionsDtos = entity.getHistoriquePositions()
                    .stream()
                    .map(positionGpsMapper::toDto)
                    .collect(Collectors.toList());
            dto.setHistoriquePositions(positionsDtos);
        } else {
            dto.setHistoriquePositions(new ArrayList<>());
        }

        return dto;
    }


    public SuiviTrajetEntity toEntity(SuiviTrajetDto dto) {
        if (dto == null) {
            return null;
        }

        SuiviTrajetEntity entity = new SuiviTrajetEntity();
        entity.setId(dto.getId());
        entity.setStatut(dto.getStatut());
        

        return entity;
    }
}