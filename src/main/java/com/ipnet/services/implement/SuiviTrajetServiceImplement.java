package com.ipnet.services.implement;

import com.ipnet.dto.SuiviTrajetDto;
import com.ipnet.entity.SuiviTrajetEntity;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.enums.StatutSuiviTrajet;
import com.ipnet.enums.StatutTrajet;
import com.ipnet.mappers.SuiviTrajetMapper;
import com.ipnet.repository.SuiviTrajetRepository;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.security.model.User;
import com.ipnet.services.interfaces.SuiviTrajetServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class SuiviTrajetServiceImplement
        implements SuiviTrajetServiceInterface {

    private final SuiviTrajetRepository suiviRepository;
    private final TrajetRepository trajetRepository;
    private final SuiviTrajetMapper suiviMapper;

    public SuiviTrajetServiceImplement(
            SuiviTrajetRepository suiviRepository,
            TrajetRepository trajetRepository,
            SuiviTrajetMapper suiviMapper
    ) {
        this.suiviRepository = suiviRepository;
        this.trajetRepository = trajetRepository;
        this.suiviMapper = suiviMapper;
    }

    @Override
    public SuiviTrajetDto creerOuRecupererSuivi(UUID trajetId) {
        TrajetEntity trajet = getTrajet(trajetId);

        SuiviTrajetEntity suivi = suiviRepository
                .findByTrajet_Id(trajetId)
                .orElseGet(() -> {
                    SuiviTrajetEntity nouveau =
                            new SuiviTrajetEntity();

                    nouveau.setTrajet(trajet);
                    nouveau.setStatut(
                            StatutSuiviTrajet.PROGRAMME
                    );
                    nouveau.setDerniereMiseAJour(
                            LocalDateTime.now()
                    );

                    return suiviRepository.save(nouveau);
                });

        return suiviMapper.toDto(suivi);
    }

    @Override
    public SuiviTrajetDto demarrerSuivi(
            UUID trajetId,
            String username
    ) {
        TrajetEntity trajet = getTrajet(trajetId);
        verifierChauffeurAffecte(trajet, username);

        SuiviTrajetEntity suivi = suiviRepository
                .findByTrajet_Id(trajetId)
                .orElseGet(() -> {
                    SuiviTrajetEntity nouveau =
                            new SuiviTrajetEntity();
                    nouveau.setTrajet(trajet);
                    return nouveau;
                });

        if (suivi.getStatut() == StatutSuiviTrajet.TERMINE) {
            throw new RuntimeException(
                    "Ce trajet est déjà terminé."
            );
        }

        if (suivi.getStatut() == StatutSuiviTrajet.ANNULE) {
            throw new RuntimeException(
                    "Ce trajet a été annulé."
            );
        }

        LocalDateTime maintenant = LocalDateTime.now();

        suivi.setStatut(StatutSuiviTrajet.EN_COURS);

        if (suivi.getDateDemarrage() == null) {
            suivi.setDateDemarrage(maintenant);
        }

        suivi.setDateFin(null);
        suivi.setDerniereMiseAJour(maintenant);

        trajet.setStatut(StatutTrajet.EN_COURS);
        trajetRepository.save(trajet);

        return suiviMapper.toDto(
                suiviRepository.save(suivi)
        );
    }

    @Override
    public SuiviTrajetDto mettreEnPause(
            Long suiviId,
            String username
    ) {
        return mettreAJourStatut(
                suiviId,
                StatutSuiviTrajet.PAUSE,
                null,
                username
        );
    }

    @Override
    public SuiviTrajetDto reprendreSuivi(
            Long suiviId,
            String username
    ) {
        return mettreAJourStatut(
                suiviId,
                StatutSuiviTrajet.EN_COURS,
                null,
                username
        );
    }

    @Override
    public SuiviTrajetDto terminerSuivi(
            Long suiviId,
            String username
    ) {
        SuiviTrajetDto dto = mettreAJourStatut(
                suiviId,
                StatutSuiviTrajet.TERMINE,
                null,
                username
        );

        SuiviTrajetEntity suivi = getSuivi(suiviId);
        TrajetEntity trajet = suivi.getTrajet();

        trajet.setStatut(StatutTrajet.TERMINE);
        trajetRepository.save(trajet);

        return dto;
    }

    @Override
    public SuiviTrajetDto annulerSuivi(
            Long suiviId,
            String username
    ) {
        SuiviTrajetDto dto = mettreAJourStatut(
                suiviId,
                StatutSuiviTrajet.ANNULE,
                null,
                username
        );

        SuiviTrajetEntity suivi = getSuivi(suiviId);
        TrajetEntity trajet = suivi.getTrajet();

        trajet.setStatut(StatutTrajet.ANNULE);
        trajetRepository.save(trajet);

        return dto;
    }

    @Override
    public SuiviTrajetDto mettreAJourStatut(
            Long suiviId,
            StatutSuiviTrajet statut,
            String message,
            String username
    ) {
        SuiviTrajetEntity suivi = getSuivi(suiviId);

        verifierChauffeurAffecte(
                suivi.getTrajet(),
                username
        );

        if (suivi.getStatut() == StatutSuiviTrajet.TERMINE) {
            throw new RuntimeException(
                    "Le suivi est déjà terminé."
            );
        }

        if (suivi.getStatut() == StatutSuiviTrajet.ANNULE) {
            throw new RuntimeException(
                    "Le suivi est déjà annulé."
            );
        }

        LocalDateTime maintenant = LocalDateTime.now();

        suivi.setStatut(statut);
        suivi.setDerniereMiseAJour(maintenant);

        if (message != null && !message.isBlank()) {
            suivi.setMessage(message.trim());
        }

        if (statut == StatutSuiviTrajet.EN_COURS
                && suivi.getDateDemarrage() == null) {
            suivi.setDateDemarrage(maintenant);
        }

        if (statut == StatutSuiviTrajet.TERMINE
                || statut == StatutSuiviTrajet.ANNULE) {
            suivi.setDateFin(maintenant);
        }

        return suiviMapper.toDto(
                suiviRepository.save(suivi)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SuiviTrajetDto getSuiviParTrajet(UUID trajetId) {
        SuiviTrajetEntity suivi = suiviRepository
                .findByTrajet_Id(trajetId)
                .orElseThrow(() -> new RuntimeException(
                        "Aucun suivi n'existe pour ce trajet."
                ));

        return suiviMapper.toDto(suivi);
    }

    @Override
    @Transactional(readOnly = true)
    public SuiviTrajetDto getSuiviParId(Long suiviId) {
        return suiviMapper.toDto(getSuivi(suiviId));
    }

    private TrajetEntity getTrajet(UUID trajetId) {
        return trajetRepository.findById(trajetId)
                .orElseThrow(() -> new RuntimeException(
                        "Trajet introuvable."
                ));
    }

    private SuiviTrajetEntity getSuivi(Long suiviId) {
        return suiviRepository.findById(suiviId)
                .orElseThrow(() -> new RuntimeException(
                        "Suivi de trajet introuvable."
                ));
    }

    private void verifierChauffeurAffecte(
            TrajetEntity trajet,
            String username
    ) {
        if (username == null || username.isBlank()) {
            throw new RuntimeException(
                    "Utilisateur connecté introuvable."
            );
        }

        User chauffeur = trajet.getChauffeur();

        if (chauffeur == null) {
            throw new RuntimeException(
                    "Aucun chauffeur n'est affecté à ce trajet."
            );
        }

        if (!chauffeur.isEnable()) {
            throw new RuntimeException(
                    "Le compte du chauffeur affecté est désactivé."
            );
        }

        if (!chauffeur.getUsername().equals(username)) {
            throw new RuntimeException(
                    "Vous n'êtes pas le chauffeur affecté à ce trajet."
            );
        }
    }
}