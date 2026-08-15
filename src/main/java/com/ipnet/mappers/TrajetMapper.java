package com.ipnet.mappers;

import com.ipnet.dto.AgenceDto;
import com.ipnet.dto.TrajetResponseDto;
import com.ipnet.dto.VehiculeDto;
import com.ipnet.dto.VilleDto;
import com.ipnet.entity.AgenceEntity;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.entity.VehiculeEntity;
import com.ipnet.entity.VilleEntity;

import org.springframework.stereotype.Component;

@Component
public class TrajetMapper {

    public TrajetResponseDto toResponse(TrajetEntity entity) {
        TrajetResponseDto dto = new TrajetResponseDto();
        dto.setId(entity.getId());
        dto.setVilleDepart(toVilleDto(entity.getVilleDepart()));
        dto.setVilleArrivee(toVilleDto(entity.getVilleArrivee()));
        dto.setVehicule(toVehiculeDto(entity.getVehicule()));
        dto.setDistance(entity.getDistance());
        dto.setDureeEstimee(entity.getDureeEstimee());
        dto.setTarif(entity.getTarif());
        dto.setDateDepart(entity.getDateDepart());
        dto.setHeureDepart(entity.getHeureDepart());
        dto.setStatut(entity.getStatut());

        if (entity.getChauffeur() != null) {
            dto.setChauffeurId(entity.getChauffeur().getPublicId());
            dto.setChauffeurNom(entity.getChauffeur().getNom());
        }

        AgenceEntity agenceDep = entity.getAgenceDepart() != null ? entity.getAgenceDepart() : entity.getAgence();
        if (agenceDep != null) {
            dto.setAgenceId(agenceDep.getId());
            dto.setAgenceNom(agenceDep.getNom());
            dto.setAgenceDepart(toAgenceDto(agenceDep));
        }

        if (entity.getAgenceArrivee() != null) {
            dto.setAgenceArrivee(toAgenceDto(entity.getAgenceArrivee()));
        }

        return dto;
    }

    private AgenceDto toAgenceDto(AgenceEntity entity) {
        if (entity == null) return null;
        AgenceDto dto = new AgenceDto();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setAdresse(entity.getAdresse());
        dto.setTelephone(entity.getTelephone());
        dto.setEmail(entity.getEmail());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setStatut(entity.getStatut());
        dto.setPhotos(entity.getPhotos());
        if (entity.getVille() != null) {
            dto.setVilleId(entity.getVille().getId());
            dto.setVilleNom(entity.getVille().getNomVille());
        }
        return dto;
    }

    private VilleDto toVilleDto(VilleEntity entity) {
        if (entity == null) return null;
        VilleDto dto = new VilleDto();
        dto.setId(entity.getId());
        dto.setNomVille(entity.getNomVille());
        dto.setRegion(entity.getRegion());
        return dto;
    }

    private VehiculeDto toVehiculeDto(VehiculeEntity entity) {
        if (entity == null) return null;
        VehiculeDto dto = new VehiculeDto();
        dto.setId(entity.getId());
        dto.setMarque(entity.getMarque());
        dto.setModele(entity.getModele());
        dto.setImmatriculation(entity.getImmatriculation());
        dto.setCapacite(entity.getCapacite());
        dto.setStatut(entity.getStatut());
        dto.setImage(entity.getImage());
        return dto;
    }
}