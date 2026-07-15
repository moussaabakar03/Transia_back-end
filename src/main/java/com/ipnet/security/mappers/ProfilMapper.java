package com.ipnet.security.mappers;


import org.springframework.stereotype.Component;

import com.ipnet.security.dto.ProfilDTO;
import com.ipnet.security.model.Profil;

@Component
public class ProfilMapper {

    public ProfilDTO toDto(Profil entity) {
        ProfilDTO dto = new ProfilDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setPhotoProfil(entity.getPhotoProfil());
        dto.setAdresse(entity.getAdresse());
        return dto;
    }

    public void updateEntity(Profil entity, ProfilDTO dto) {
        if (dto.getPhotoProfil() != null) {
            entity.setPhotoProfil(dto.getPhotoProfil());
        }
        if (dto.getAdresse() != null) {
            entity.setAdresse(dto.getAdresse());
        }
    }
}