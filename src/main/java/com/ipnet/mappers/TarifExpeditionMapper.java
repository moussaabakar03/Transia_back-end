package com.ipnet.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ipnet.dto.TarifExpeditionDto;
import com.ipnet.entity.TarifExpeditionEntity;

@Component
public class TarifExpeditionMapper {

    public TarifExpeditionDto toDto(TarifExpeditionEntity entity) {
        if (entity == null) {
            return null;
        }

        TarifExpeditionDto dto = new TarifExpeditionDto();
        dto.setId(entity.getId());
        dto.setTranchePoids(entity.getTranchePoids());
        dto.setTarif(entity.getTarif());

        if (entity.getVilleDepart() != null) {
            dto.setVilleDepartId(entity.getVilleDepart().getId());
            dto.setVilleDepartNom(entity.getVilleDepart().getNomVille());
        }
        if (entity.getVilleArrivee() != null) {
            dto.setVilleArriveeId(entity.getVilleArrivee().getId());
            dto.setVilleArriveeNom(entity.getVilleArrivee().getNomVille());
        }

        return dto;
    }

    public List<TarifExpeditionDto> toDtoList(List<TarifExpeditionEntity> entities) {
        return entities.stream().map(this::toDto).toList();
    }
}
