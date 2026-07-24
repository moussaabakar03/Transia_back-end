package com.ipnet.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ipnet.dto.DemandeCollecteDto;
import com.ipnet.entity.DemandeCollecteEntity;

@Component
public class DemandeCollecteMapper {

    public DemandeCollecteDto toDto(DemandeCollecteEntity entity) {
        if (entity == null) {
            return null;
        }

        DemandeCollecteDto dto = new DemandeCollecteDto();
        dto.setId(entity.getId());
        dto.setAdresseCollecte(entity.getAdresseCollecte());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setDateHeureCollecte(entity.getDateHeureCollecte());
        dto.setStatut(entity.getStatut());

        if (entity.getExpediteur() != null) {
            dto.setExpediteurId(entity.getExpediteur().getPublicId());
            dto.setExpediteurNom(entity.getExpediteur().getNom());
        }
        if (entity.getAgence() != null) {
            dto.setAgenceId(entity.getAgence().getId());
            dto.setAgenceNom(entity.getAgence().getNom());
        }
        if (entity.getLivreur() != null) {
            dto.setLivreurId(entity.getLivreur().getPublicId());
            dto.setLivreurNom(entity.getLivreur().getNom());
        }
        if (entity.getColis() != null) {
            dto.setColisId(entity.getColis().getId());
            dto.setColisNumeroSuivi(entity.getColis().getNumeroSuivi());
        }
        if (entity.getTournee() != null) {
            dto.setTourneeId(entity.getTournee().getId());
        }

        return dto;
    }

    public List<DemandeCollecteDto> toDtoList(List<DemandeCollecteEntity> entities) {
        return entities.stream().map(this::toDto).toList();
    }
}
