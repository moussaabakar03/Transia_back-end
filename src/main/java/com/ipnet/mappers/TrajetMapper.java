package com.ipnet.mappers;

import com.ipnet.dto.TrajetResponseDto;
import com.ipnet.entity.TrajetEntity;
import org.springframework.stereotype.Component;

@Component
public class TrajetMapper {

    public TrajetResponseDto toResponse(TrajetEntity entity) {
        if (entity == null) return null;

        TrajetResponseDto response = new TrajetResponseDto();
        response.setId(entity.getId());
        
        // Extraction des noms depuis les entités liées
        if (entity.getVilleDepart() != null) {
            response.setVilleDepartNom(entity.getVilleDepart().getNomVille());
        }
        if (entity.getVilleArrivee() != null) {
            response.setVilleArriveeNom(entity.getVilleArrivee().getNomVille());
        }
        if (entity.getVehicule() != null) {
            response.setVehiculeImmatriculation(entity.getVehicule().getImmatriculation());
        }

        response.setDistance(entity.getDistance());
        response.setDureeEstimee(entity.getDureeEstimee());
        response.setTarif(entity.getTarif());
        response.setDateDepart(entity.getDateDepart());
        response.setHeureDepart(entity.getHeureDepart());
        response.setStatut(entity.getStatut());

        return response;
    }
}