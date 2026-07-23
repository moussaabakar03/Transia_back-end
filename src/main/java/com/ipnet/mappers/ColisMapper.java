package com.ipnet.mappers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipnet.dto.ColisDto;
import com.ipnet.dto.ColisRequestDto;
import com.ipnet.dto.HistoriqueColisDto;
import com.ipnet.entity.Colis;
import com.ipnet.security.mappers.UserMapper;

@Component
public class ColisMapper {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired(required = false)
    private HistoriqueColisMapper historiqueColisMapper;

    public ColisDto toDto(Colis entity) {
        if (entity == null) {
            return null;
        }

        ColisDto dto = new ColisDto();
        dto.setId(entity.getId());
        dto.setNumeroSuivi(entity.getNumeroSuivi());
        dto.setNomDestinataire(entity.getNomDestinataire());
        dto.setAdresseDestinataire(entity.getAdresseDestinataire());
        dto.setTelephoneDestinataire(entity.getTelephoneDestinataire());
        dto.setPoids(entity.getPoids());
        dto.setLongueur(entity.getLongueur());
        dto.setLargeur(entity.getLargeur());
        dto.setHauteur(entity.getHauteur());
        dto.setStatut(entity.getStatut());
        dto.setRemarques(entity.getRemarques());
        dto.setDateCreation(entity.getDateCreationColis());
        dto.setDateLivraison(entity.getDateLivraison());
        dto.setModeDepot(entity.getModeDepot());
        dto.setAdresseCollecte(entity.getAdresseCollecte());
        dto.setTelephoneCollecte(entity.getTelephoneCollecte());
        dto.setDateHeureCollecteSouhaitee(entity.getDateHeureCollecteSouhaitee());
        dto.setLatitudeDestinataire(entity.getLatitudeDestinataire());
        dto.setLongitudeDestinataire(entity.getLongitudeDestinataire());
        dto.setLatitudeCollecte(entity.getLatitudeCollecte());
        dto.setLongitudeCollecte(entity.getLongitudeCollecte());

        if (entity.getExpediteur() != null && userMapper != null) { 
            dto.setExpediteur(userMapper.mapToUserDTO(entity.getExpediteur()));
            dto.setExpediteurId(entity.getExpediteur().getPublicId());
        }

        if (entity.getLivreur() != null && userMapper != null) {
            dto.setLivreur(userMapper.mapToUserDTO(entity.getLivreur()));
            dto.setLivreurId(entity.getLivreur().getPublicId());
        }

        if (entity.getTournee() != null) {
            dto.setTourneeId(entity.getTournee().getId());
        }
        if (entity.getVilleDepart() != null) {
            dto.setVilleDepartId(entity.getVilleDepart().getId());
            dto.setVilleDepartNom(entity.getVilleDepart().getNomVille());
        }
        if (entity.getVilleArrivee() != null) {
            dto.setVilleArriveeId(entity.getVilleArrivee().getId());
            dto.setVilleArriveeNom(entity.getVilleArrivee().getNomVille());
        }
        if (entity.getTrajet() != null) {
            dto.setTrajetId(entity.getTrajet().getId());
        }
        dto.setModeRemise(entity.getModeRemise());
        dto.setQrCode(entity.getQrCode());

        if (entity.getHistorique() != null && historiqueColisMapper != null) {
            dto.setHistorique(entity.getHistorique().stream()
                    .map(historiqueColisMapper::toDto)
                    .toList());
        }

        return dto;
    }

    public List<ColisDto> toDtoList(List<Colis> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    public Colis toEntity(ColisRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Colis entity = new Colis();
        entity.setNomDestinataire(dto.getNomDestinataire());
        entity.setAdresseDestinataire(dto.getAdresseDestinataire());
        entity.setTelephoneDestinataire(dto.getTelephoneDestinataire());
        entity.setPoids(dto.getPoids());
        entity.setLongueur(dto.getLongueur());
        entity.setLargeur(dto.getLargeur());
        entity.setHauteur(dto.getHauteur());
        entity.setRemarques(dto.getRemarques());
        entity.setModeDepot(dto.getModeDepot());
        entity.setAdresseCollecte(dto.getAdresseCollecte());
        entity.setTelephoneCollecte(dto.getTelephoneCollecte());
        entity.setDateHeureCollecteSouhaitee(dto.getDateHeureCollecteSouhaitee());
        entity.setLatitudeDestinataire(dto.getLatitudeDestinataire());
        entity.setLongitudeDestinataire(dto.getLongitudeDestinataire());
        entity.setLatitudeCollecte(dto.getLatitudeCollecte());
        entity.setLongitudeCollecte(dto.getLongitudeCollecte());

        return entity;
    }
}
