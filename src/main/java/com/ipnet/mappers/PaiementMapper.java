package com.ipnet.mappers;


import org.springframework.stereotype.Component;

import com.ipnet.dto.PaiementRequestDto;
import com.ipnet.entity.PaiementEntity;

@Component
public class PaiementMapper {

    public PaiementRequestDto toDto(PaiementEntity entity) {
    	PaiementRequestDto dto = new PaiementRequestDto();
        
    	dto.setId(entity.getId());
    	dto.setModePaiement(entity.getModePaiement());
    	dto.setMontantVerse(entity.getMontant());
    	dto.setReference(entity.getReference());
    	dto.setReservationId(entity.getReservation());
    	
        return dto;
    }
}
