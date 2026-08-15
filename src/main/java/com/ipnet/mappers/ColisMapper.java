package com.ipnet.mappers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipnet.dto.ColisDto;
import com.ipnet.dto.ColisStatutDto;
import com.ipnet.entity.Colis;

@Component
public class ColisMapper {

    @Autowired(required = false)
    private HistoriqueColisMapper historiqueColisMapper;

    public ColisDto toDto(Colis entity) {
        if (entity == null) {
            return null;
        }

        ColisDto dto = new ColisDto();
        dto.setId(entity.getId());
        dto.setNumeroSuivi(entity.getNumeroSuivi());
        dto.setCodeRetrait(entity.getCodeRetrait());
        
        String refSuivi = (entity.getNumeroSuivi() != null && !entity.getNumeroSuivi().trim().isEmpty())
                ? entity.getNumeroSuivi()
                : String.valueOf(entity.getId());
        String urlSuivi = "http://localhost:4200/suivi/" + refSuivi;
        dto.setLienSuivi(urlSuivi);
        dto.setQrCode(urlSuivi);

        dto.setDescription(entity.getDescription());
        dto.setTranchePoids(entity.getTranchePoids());
        dto.setPoidsReel(entity.getPoidsReel());
        dto.setDimensions(entity.getDimensions());
        dto.setStatut(entity.getStatut());
        dto.setStatutPaiement(entity.getStatutPaiement());
        dto.setModeRemise(entity.getModeRemise());
        dto.setExpediteurNom(entity.getExpediteurNom());
        dto.setExpediteurTelephone(entity.getExpediteurTelephone());
        dto.setDestinataireNom(entity.getDestinataireNom());
        dto.setDestinataireTelephone(entity.getDestinataireTelephone());
        dto.setDestinataireAdresse(entity.getDestinataireAdresse());
        dto.setAdresseCollecte(entity.getAdresseCollecte());
        dto.setLatitudeCollecte(entity.getLatitudeCollecte());
        dto.setLongitudeCollecte(entity.getLongitudeCollecte());
        dto.setPrixEstime(entity.getPrixEstime());
        dto.setPrixFinal(entity.getPrixFinal());
        dto.setFraisCollecte(entity.getFraisCollecte());
        dto.setFraisLivraison(entity.getFraisLivraison());
        dto.setDateCreation(entity.getDateCreationColis());
        dto.setDateLivraison(entity.getDateLivraison());

        if (entity.getAgenceDepart() != null) {
            dto.setAgenceDepartId(entity.getAgenceDepart().getId());
            dto.setAgenceDepartNom(entity.getAgenceDepart().getNom());
        }
        if (entity.getAgenceArrivee() != null) {
            dto.setAgenceArriveeId(entity.getAgenceArrivee().getId());
            dto.setAgenceArriveeNom(entity.getAgenceArrivee().getNom());
        }
        if (entity.getTrajet() != null) {
            dto.setTrajetId(entity.getTrajet().getId());
            dto.setTrajetInfo(construireTrajetInfo(entity.getTrajet()));
        }
        if (entity.getAgentEnregistreur() != null) {
            dto.setAgentEnregistreurId(entity.getAgentEnregistreur().getPublicId());
            dto.setAgentEnregistreurNom(entity.getAgentEnregistreur().getNom());
        }
        if (entity.getLivreur() != null) {
            dto.setLivreurId(entity.getLivreur().getPublicId());
            dto.setLivreurNom(entity.getLivreur().getNom());
        }

        if (entity.getHistorique() != null && historiqueColisMapper != null) {
            dto.setHistorique(entity.getHistorique().stream()
                    .map(historiqueColisMapper::toDto)
                    .toList());
        }

        return dto;
    }

    public List<ColisDto> toDtoList(List<Colis> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public ColisStatutDto toStatutDto(Colis entity) {
        if (entity == null) {
            return null;
        }

        ColisStatutDto dto = new ColisStatutDto();
        dto.setId(entity.getId());
        dto.setNumeroSuivi(entity.getNumeroSuivi());
        dto.setStatut(entity.getStatut());
        dto.setDescription(entity.getDescription());
        dto.setModeRemise(entity.getModeRemise());
        dto.setCodeRetrait(entity.getCodeRetrait());
        String refSuiviStatut = (entity.getNumeroSuivi() != null && !entity.getNumeroSuivi().trim().isEmpty())
                ? entity.getNumeroSuivi()
                : String.valueOf(entity.getId());
        String urlSuiviStatut = "http://localhost:4200/suivi/" + refSuiviStatut;
        dto.setLienSuivi(urlSuiviStatut);
        dto.setQrCode(urlSuiviStatut);
        dto.setDateCreation(entity.getDateCreationColis());
        dto.setDateLivraison(entity.getDateLivraison());

        if (entity.getAgenceDepart() != null) {
            dto.setAgenceDepartNom(entity.getAgenceDepart().getNom());
        }
        if (entity.getAgenceArrivee() != null) {
            dto.setAgenceArriveeNom(entity.getAgenceArrivee().getNom());
        }
        if (entity.getTrajet() != null) {
            dto.setTrajetId(entity.getTrajet().getId());
            dto.setTrajetInfo(construireTrajetInfo(entity.getTrajet()));
        }

        if (entity.getHistorique() != null && historiqueColisMapper != null) {
            dto.setHistorique(entity.getHistorique().stream()
                    .map(historiqueColisMapper::toDto)
                    .toList());
        }

        return dto;
    }

    private String construireTrajetInfo(com.ipnet.entity.TrajetEntity trajet) {
        if (trajet == null) return null;
        String dep = trajet.getVilleDepart() != null ? trajet.getVilleDepart().getNomVille() : (trajet.getAgenceDepart() != null ? trajet.getAgenceDepart().getNom() : "");
        String arr = trajet.getVilleArrivee() != null ? trajet.getVilleArrivee().getNomVille() : (trajet.getAgenceArrivee() != null ? trajet.getAgenceArrivee().getNom() : "");
        String date = trajet.getDateDepart() != null ? trajet.getDateDepart().toString() : "";
        String heure = trajet.getHeureDepart() != null ? trajet.getHeureDepart().toString() : "";
        return (dep + " → " + arr + (date.isEmpty() ? "" : " (" + date + " " + heure + ")")).trim();
    }
}
