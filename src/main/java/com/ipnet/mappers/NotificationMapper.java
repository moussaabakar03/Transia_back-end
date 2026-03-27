package com.ipnet.mappers;

import com.ipnet.dto.NotificationDto;
import com.ipnet.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDto(NotificationEntity entity) {
        if (entity == null) return null;
        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setTitre(entity.getTitre());
        dto.setMessage(entity.getMessage());
        dto.setLu(entity.isLu());
        dto.setUserId(entity.getDestinataire().getId());
        return dto;
    }
}