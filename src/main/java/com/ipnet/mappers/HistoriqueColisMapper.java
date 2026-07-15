package com.ipnet.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipnet.dto.HistoriqueColisDto;
import com.ipnet.entity.HistoriqueColis;
import com.ipnet.security.mappers.UserMapper;

@Component
public class HistoriqueColisMapper {

    @Autowired(required = false)
    private UserMapper userMapper;

    public HistoriqueColisDto toDto(HistoriqueColis entity) {
        if (entity == null) {
            return null;
        }

        HistoriqueColisDto dto = new HistoriqueColisDto();
        dto.setId(entity.getId());
        dto.setAncienStatut(entity.getAncienStatut());
        dto.setNouveauStatut(entity.getNouveauStatut());
        dto.setDateChangement(entity.getDateChangement());
        dto.setCommentaire(entity.getCommentaire());

        if (entity.getColis() != null) {
            dto.setColisId(entity.getColis().getId());
        }

        if (entity.getUtilisateur() != null && userMapper != null) {
            dto.setUtilisateur(userMapper.mapToUserDTO(entity.getUtilisateur()));
            dto.setUtilisateurId(entity.getUtilisateur().getPublicId());
        }
 
        return dto;
    }
}
