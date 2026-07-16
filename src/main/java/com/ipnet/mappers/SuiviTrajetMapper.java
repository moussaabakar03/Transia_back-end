package com.ipnet.mappers;

import com.ipnet.dto.PositionGpsDto;
import com.ipnet.dto.SuiviTrajetDto;
import com.ipnet.entity.SuiviTrajetEntity;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.entity.VehiculeEntity;
import com.ipnet.repository.PositionGpsRepository;
import org.springframework.stereotype.Component;

@Component
public class SuiviTrajetMapper {

    private final PositionGpsMapper positionGpsMapper;
    private final PositionGpsRepository positionGpsRepository;

    public SuiviTrajetMapper(
            PositionGpsMapper positionGpsMapper,
            PositionGpsRepository positionGpsRepository
    ) {
        this.positionGpsMapper = positionGpsMapper;
        this.positionGpsRepository = positionGpsRepository;
    }

    public SuiviTrajetDto toDto(SuiviTrajetEntity entity) {
        if (entity == null) {
            return null;
        }

        SuiviTrajetDto dto = new SuiviTrajetDto();

        dto.setId(entity.getId());

        if (entity.getStatut() != null) {
            dto.setStatut(entity.getStatut().name());
        }

        dto.setDateDemarrage(entity.getDateDemarrage());
        dto.setDateFin(entity.getDateFin());
        dto.setDerniereMiseAJour(
                entity.getDerniereMiseAJour()
        );
        dto.setMessage(entity.getMessage());

        TrajetEntity trajet = entity.getTrajet();

        if (trajet != null) {
            dto.setTrajetId(trajet.getId());

            if (trajet.getVilleDepart() != null) {
                dto.setVilleDepart(
                        trajet.getVilleDepart().getNomVille()
                );
            }

            if (trajet.getVilleArrivee() != null) {
                dto.setVilleArrivee(
                        trajet.getVilleArrivee().getNomVille()
                );
            }

            if (trajet.getChauffeur() != null) {
                dto.setChauffeurId(
                        trajet.getChauffeur().getId()
                );
                dto.setChauffeurNom(
                        trajet.getChauffeur().getNom()
                );
            }

            VehiculeEntity vehicule = trajet.getVehicule();

            if (vehicule != null) {
                dto.setVehicule(
                        vehicule.getMarque()
                                + " "
                                + vehicule.getModele()
                );
                dto.setImmatriculation(
                        vehicule.getImmatriculation()
                );
            }

            if (trajet.getDateDepart() != null) {
                dto.setDateDepart(
                        trajet.getDateDepart().toString()
                );
            }

            dto.setHeureDepart(trajet.getHeureDepart());
        }

        PositionGpsDto dernierePosition =
                positionGpsRepository
                        .findFirstBySuiviTrajet_IdOrderByDateHeureDesc(
                                entity.getId()
                        )
                        .map(positionGpsMapper::toDto)
                        .orElse(null);

        dto.setDernierePosition(dernierePosition);

        return dto;
    }
}