package com.ipnet.services.implement;

import com.ipnet.dto.PositionGpsDto;
import com.ipnet.entity.PositionGpsEntity;
import com.ipnet.entity.SuiviTrajetEntity;
import com.ipnet.enums.StatutSuiviTrajet;
import com.ipnet.mappers.PositionGpsMapper;
import com.ipnet.repository.PositionGpsRepository;
import com.ipnet.repository.SuiviTrajetRepository;
import com.ipnet.security.model.User;
import com.ipnet.services.interfaces.PositionGpsServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PositionGpsServiceImplement
        implements PositionGpsServiceInterface {

    private final PositionGpsRepository positionGpsRepository;
    private final SuiviTrajetRepository suiviTrajetRepository;
    private final PositionGpsMapper positionGpsMapper;

    public PositionGpsServiceImplement(
            PositionGpsRepository positionGpsRepository,
            SuiviTrajetRepository suiviTrajetRepository,
            PositionGpsMapper positionGpsMapper
    ) {
        this.positionGpsRepository = positionGpsRepository;
        this.suiviTrajetRepository = suiviTrajetRepository;
        this.positionGpsMapper = positionGpsMapper;
    }

    @Override
    public PositionGpsDto enregistrerPosition(
            PositionGpsDto dto,
            String username
    ) {
        verifierDto(dto);

        SuiviTrajetEntity suivi = suiviTrajetRepository
                .findById(dto.getSuiviTrajetId())
                .orElseThrow(() -> new RuntimeException(
                        "Suivi de trajet introuvable."
                ));

        verifierChauffeurAffecte(suivi, username);

        if (suivi.getStatut()
                != StatutSuiviTrajet.EN_COURS) {
            throw new RuntimeException(
                    "La position GPS ne peut être envoyée "
                            + "que lorsque le trajet est en cours."
            );
        }

        PositionGpsEntity entity =
                positionGpsMapper.toEntity(dto);

        entity.setDateHeure(LocalDateTime.now());
        entity.setSuiviTrajet(suivi);

        suivi.setDerniereMiseAJour(
                entity.getDateHeure()
        );

        suiviTrajetRepository.save(suivi);

        PositionGpsEntity saved =
                positionGpsRepository.save(entity);

        return positionGpsMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PositionGpsDto getDernierePosition(
            Long suiviTrajetId
    ) {
        verifierExistenceSuivi(suiviTrajetId);

        return positionGpsRepository
                .findFirstBySuiviTrajet_IdOrderByDateHeureDesc(
                        suiviTrajetId
                )
                .map(positionGpsMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionGpsDto> getHistoriquePositions(
            Long suiviTrajetId
    ) {
        verifierExistenceSuivi(suiviTrajetId);

        return positionGpsRepository
                .findBySuiviTrajet_IdOrderByDateHeureAsc(
                        suiviTrajetId
                )
                .stream()
                .map(positionGpsMapper::toDto)
                .collect(Collectors.toList());
    }

    private void verifierDto(PositionGpsDto dto) {
        if (dto == null) {
            throw new RuntimeException(
                    "Les informations GPS sont obligatoires."
            );
        }

        if (dto.getSuiviTrajetId() == null) {
            throw new RuntimeException(
                    "L'identifiant du suivi est obligatoire."
            );
        }

        if (dto.getLatitude() == null
                || dto.getLongitude() == null) {
            throw new RuntimeException(
                    "La latitude et la longitude sont obligatoires."
            );
        }

        if (dto.getLatitude() < -90
                || dto.getLatitude() > 90) {
            throw new RuntimeException(
                    "La latitude est invalide."
            );
        }

        if (dto.getLongitude() < -180
                || dto.getLongitude() > 180) {
            throw new RuntimeException(
                    "La longitude est invalide."
            );
        }

        if (dto.getVitesse() != null
                && dto.getVitesse() < 0) {
            throw new RuntimeException(
                    "La vitesse ne peut pas être négative."
            );
        }
    }

    private void verifierExistenceSuivi(Long suiviTrajetId) {
        if (!suiviTrajetRepository.existsById(suiviTrajetId)) {
            throw new RuntimeException(
                    "Suivi de trajet introuvable."
            );
        }
    }

    private void verifierChauffeurAffecte(
            SuiviTrajetEntity suivi,
            String username
    ) {
        if (username == null || username.isBlank()) {
            throw new RuntimeException(
                    "Utilisateur connecté introuvable."
            );
        }

        User chauffeur =
                suivi.getTrajet().getChauffeur();

        if (chauffeur == null) {
            throw new RuntimeException(
                    "Aucun chauffeur n'est affecté à ce trajet."
            );
        }

        if (!chauffeur.getTelephone().equals(username)) {
            throw new RuntimeException(
                    "Vous n'êtes pas le chauffeur affecté à ce trajet."
            );
        }
    }
}