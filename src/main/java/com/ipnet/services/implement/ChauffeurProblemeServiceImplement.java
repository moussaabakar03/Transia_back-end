package com.ipnet.services.implement;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipnet.dto.ChauffeurProblemeDto;
import com.ipnet.entity.ChauffeurProblemeEntity;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.repository.ChauffeurProblemeRepository;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.security.model.User;
import com.ipnet.services.interfaces.ChauffeurProblemeServiceInterface;

@Service
public class ChauffeurProblemeServiceImplement
        implements ChauffeurProblemeServiceInterface {

    @Autowired
    private ChauffeurProblemeRepository repository;

    @Autowired
    private TrajetRepository trajetRepository;

    @Override
    public ChauffeurProblemeDto save(ChauffeurProblemeDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException(
                    "Les informations du signalement sont obligatoires."
            );
        }

        if (dto.getTrajetId() == null) {
            throw new IllegalArgumentException(
                    "L'identifiant du trajet est obligatoire."
            );
        }

        if (dto.getTypeProbleme() == null
                || dto.getTypeProbleme().isBlank()) {
            throw new IllegalArgumentException(
                    "Le type de problème est obligatoire."
            );
        }

        if (dto.getDescription() == null
                || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException(
                    "La description du problème est obligatoire."
            );
        }

        TrajetEntity trajet = trajetRepository
                .findById(dto.getTrajetId())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Trajet introuvable."
                        )
                );

        /*
         * Le chauffeur est récupéré directement depuis le trajet.
         * Il n'est donc plus nécessaire de rechercher un User avec
         * l'identifiant UUID reçu depuis Flutter.
         */
        User chauffeur = trajet.getChauffeur();

        if (chauffeur == null) {
            throw new RuntimeException(
                    "Aucun chauffeur n'est affecté à ce trajet."
            );
        }

        /*
         * Lorsque Flutter envoie chauffeurId, on vérifie qu'il correspond
         * au publicId du chauffeur réellement affecté au trajet.
         */
        if (dto.getChauffeurId() != null
                && chauffeur.getPublicId() != null
                && !dto.getChauffeurId().equals(
                        chauffeur.getPublicId()
                )) {
            throw new RuntimeException(
                    "Ce chauffeur n'est pas affecté à ce trajet."
            );
        }

        ChauffeurProblemeEntity entity =
                new ChauffeurProblemeEntity();

        entity.setTrajet(trajet);
        entity.setChauffeur(chauffeur);
        entity.setTypeProbleme(
                dto.getTypeProbleme().trim()
        );
        entity.setDescription(
                dto.getDescription().trim()
        );

        String statut =
                dto.getStatut() == null
                        || dto.getStatut().isBlank()
                        ? "EN_ATTENTE"
                        : dto.getStatut().trim();

        entity.setStatut(statut);
        entity.setCreerPar(chauffeur.getNom());

        ChauffeurProblemeEntity saved =
                repository.save(entity);

        return toDto(saved);
    }

    @Override
    public List<ChauffeurProblemeDto> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChauffeurProblemeDto> getByTrajet(
            UUID trajetId
    ) {
        return repository.findByTrajet_Id(trajetId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /*
     * Cette méthode utilise encore l'identifiant numérique interne,
     * car la relation JPA User.id est actuellement de type Long.
     */
    @Override
    public List<ChauffeurProblemeDto> getByChauffeur(
            Long chauffeurId
    ) {
        return repository.findByChauffeur_Id(chauffeurId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ChauffeurProblemeDto toDto(
            ChauffeurProblemeEntity entity
    ) {
        ChauffeurProblemeDto dto =
                new ChauffeurProblemeDto();

        dto.setId(entity.getId());

        if (entity.getTrajet() != null) {
            dto.setTrajetId(
                    entity.getTrajet().getId()
            );

            String villeDepart =
                    entity.getTrajet().getVilleDepart() != null
                            ? entity.getTrajet()
                                    .getVilleDepart()
                                    .getNomVille()
                            : "";

            String villeArrivee =
                    entity.getTrajet().getVilleArrivee() != null
                            ? entity.getTrajet()
                                    .getVilleArrivee()
                                    .getNomVille()
                            : "";

            dto.setTrajetLabel(
                    villeDepart + " → " + villeArrivee
            );
        }

        if (entity.getChauffeur() != null) {
            /*
             * L'API retourne le publicId UUID du chauffeur,
             * pas son identifiant numérique interne.
             */
            dto.setChauffeurId(
                    entity.getChauffeur().getPublicId()
            );

            dto.setChauffeurNom(
                    entity.getChauffeur().getNom()
            );
        }

        dto.setTypeProbleme(
                entity.getTypeProbleme()
        );
        dto.setDescription(
                entity.getDescription()
        );
        dto.setStatut(
                entity.getStatut()
        );
        dto.setDateCreation(
                entity.getDateCreation()
        );

        return dto;
    }
}