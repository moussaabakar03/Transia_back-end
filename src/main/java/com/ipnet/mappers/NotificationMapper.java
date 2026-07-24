package com.ipnet.mappers;

import org.springframework.stereotype.Component;

import com.ipnet.dto.NotificationDto;
import com.ipnet.entity.NotificationEntity;

@Component
public class NotificationMapper {

    public NotificationDto toDto(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }

        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setTitre(entity.getTitre());
        dto.setMessage(entity.getMessage());
        dto.setLu(entity.isLu());
        dto.setType(entity.getType());
        dto.setReferenceMetier(entity.getReferenceMetier());
        dto.setDateEnvoi(entity.getDateEnvoi());

        if (entity.getDestinataire() != null) {
            dto.setUserId(entity.getDestinataire().getId());
        }

        return dto;
    }
}
