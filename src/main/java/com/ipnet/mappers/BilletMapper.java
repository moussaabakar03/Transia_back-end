package com.ipnet.mappers;

import org.springframework.stereotype.Component;
import com.ipnet.dto.BilletDto;
import com.ipnet.entity.BilletEntity;

@Component
public class BilletMapper {

    public BilletDto toDto(BilletEntity entity) {
        BilletDto dto = new BilletDto();
        dto.setId(entity.getId());
        dto.setQrCode(entity.getQrCode());
        dto.setNomPassager(entity.getNomPassager());
        dto.setStatut(entity.getStatut());
        dto.setDateEmission(entity.getDateEmission());
        return dto;
    }
}