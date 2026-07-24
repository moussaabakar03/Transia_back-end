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
        dto.setNumeroSiege(entity.getNumeroSiege());
        if (entity.getReservation() != null) {
            dto.setReservationId(entity.getReservation().getId());
            var trajet = entity.getReservation().getTrajet();
            if (trajet != null) {
                dto.setTrajetId(trajet.getId());
                if (trajet.getDateDepart() != null) {
                    dto.setDateDepart(trajet.getDateDepart().toString());
                }
                if (trajet.getHeureDepart() != null) {
                    dto.setHeureDepart(trajet.getHeureDepart().toString());
                }
                if (trajet.getVilleDepart() != null && trajet.getVilleArrivee() != null) {
                    dto.setTrajetInfo(trajet.getVilleDepart().getNomVille() + " → " + trajet.getVilleArrivee().getNomVille());
                }
            }
        }
        return dto;
    }
}