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
        dto.setTelephone(entity.getTelephone());
        dto.setNomComplet(entity.getNomComplet());
        dto.setAdresse(entity.getAdresse());
        return dto;
    }

    public void updateEntity(Profil entity, ProfilDTO dto) {
        entity.setPhotoProfil(dto.getPhotoProfil());
        entity.setTelephone(dto.getTelephone());
        entity.setNomComplet(dto.getNomComplet());
        entity.setAdresse(dto.getAdresse());
    }
}