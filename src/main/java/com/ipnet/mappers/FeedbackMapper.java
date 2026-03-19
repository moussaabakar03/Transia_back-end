package com.ipnet.mappers;

import com.ipnet.dto.FeedbackResponseDto;
import com.ipnet.entity.FeedbackEntity;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {

    public FeedbackResponseDto toDto(FeedbackEntity entity) {
        if (entity == null) {
            return null;
        }

        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setId(entity.getId());
        dto.setNote(entity.getNote());
        dto.setCommentaire(entity.getCommentaire());
        dto.setDateCreation(entity.getDateCreation());

        // Correction ici : on utilise getId() au lieu de getIdTrajet()
        if (entity.getTrajet() != null) {
            dto.setTrajetId(entity.getTrajet().getId()); 
        }

        if (entity.getUser() != null) {
            dto.setUsername(entity.getUser().getUsername());
        }

        return dto;
    }
}