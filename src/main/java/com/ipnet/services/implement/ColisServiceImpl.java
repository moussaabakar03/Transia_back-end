package com.ipnet.services.implement;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.dto.ColisDto;
import com.ipnet.dto.ColisRequestDto;
import com.ipnet.dto.HistoriqueColisDto;
import com.ipnet.entity.Colis;
import com.ipnet.entity.HistoriqueColis;
import com.ipnet.enums.ModeDepot;
import com.ipnet.enums.StatutColis;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.entity.VilleEntity;
import com.ipnet.mappers.ColisMapper;
import com.ipnet.mappers.HistoriqueColisMapper;
import com.ipnet.repository.ColisRepository;
import com.ipnet.repository.HistoriqueColisRepository;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.repository.VilleRepository;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.ColisServiceInterface;

@Service
public class ColisServiceImpl implements ColisServiceInterface {

    private final ColisRepository colisRepository;
    private final HistoriqueColisRepository historiqueColisRepository;
    private final UserRepository userRepository;
    private final ColisMapper colisMapper;
    private final HistoriqueColisMapper historiqueColisMapper;
    private final VilleRepository villeRepository;
    private final TrajetRepository trajetRepository;

    public ColisServiceImpl(
            ColisRepository colisRepository,
            HistoriqueColisRepository historiqueColisRepository,
            UserRepository userRepository,
            ColisMapper colisMapper,
            HistoriqueColisMapper historiqueColisMapper,
            VilleRepository villeRepository,
            TrajetRepository trajetRepository) {
        this.colisRepository = colisRepository;
        this.historiqueColisRepository = historiqueColisRepository;
        this.userRepository = userRepository;
        this.colisMapper = colisMapper;
        this.historiqueColisMapper = historiqueColisMapper;
        this.villeRepository = villeRepository;
        this.trajetRepository = trajetRepository;
    }

    @Override
    @Transactional
    public ColisDto create(ColisRequestDto dto) {
        User expediteur;
        if (dto.getExpediteurId() != null) {
            expediteur = userRepository.findByPublicId(dto.getExpediteurId())
                    .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        } else {
            String currentTelephone = SecurityContextHolder.getContext().getAuthentication().getName();
            expediteur = userRepository.findByTelephone(currentTelephone)
                    .orElseThrow(() -> new RuntimeException("Utilisateur authentifié introuvable"));
        }

        Colis colis = colisMapper.toEntity(dto);
        colis.setExpediteur(expediteur);
        colis.setNumeroSuivi(generateNumeroSuivi());
        colis.setDateCreationColis(LocalDateTime.now());
        colis.setStatut(StatutColis.EN_ATTENTE_COLLECTE);
        colis.setQrCode(UUID.randomUUID().toString());
        colis.setModeRemise(dto.getModeRemise());

        if (dto.getVilleDepartId() != null) {
            VilleEntity depart = villeRepository.findById(dto.getVilleDepartId())
                    .orElseThrow(() -> new RuntimeException("Ville de départ introuvable"));
            colis.setVilleDepart(depart);
        }
        if (dto.getVilleArriveeId() != null) {
            VilleEntity arrivee = villeRepository.findById(dto.getVilleArriveeId())
                    .orElseThrow(() -> new RuntimeException("Ville d'arrivée introuvable"));
            colis.setVilleArrivee(arrivee);
        }
        if (dto.getTrajetId() != null) {
            TrajetEntity trajet = trajetRepository.findById(dto.getTrajetId())
                    .orElseThrow(() -> new RuntimeException("Trajet introuvable"));
            colis.setTrajet(trajet);
        }

        Colis savedColis = colisRepository.save(colis);
        addHistorique(savedColis, null, StatutColis.EN_ATTENTE_COLLECTE, null, "Création du colis");

        return colisMapper.toDto(savedColis);
    }

    @Override
    @Transactional
    public ColisDto createDemandeEnlevement(ColisRequestDto dto) {
        dto.setModeDepot(ModeDepot.ENLEVEMENT_DOMICILE);
        return create(dto);
    }

    @Override
    public ColisDto getById(UUID id) {
        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé avec l'ID : " + id));
        return colisMapper.toDto(colis);
    }

    @Override
    public ColisDto getByNumeroSuivi(String numeroSuivi) {
        List<Colis> colisList = colisRepository.findByNumeroSuivi(numeroSuivi);
        if (colisList.isEmpty()) {
            throw new RuntimeException("Colis non trouvé avec le numéro de suivi : " + numeroSuivi);
        }
        return colisMapper.toDto(colisList.get(0));
    }

    @Override
    public List<ColisDto> listColis() {
        return colisMapper.toDtoList(colisRepository.findAll());
    }

    @Override
    public List<ColisDto> filterColis(StatutColis statut, UUID livreurId, UUID expediteurId, String search) {
        return colisMapper.toDtoList(colisRepository.findByFilters(statut, livreurId, expediteurId, search));
    }

    @Override
    public List<ColisDto> findNearby(Double latitude, Double longitude, Double distanceKm) {
        return colisMapper.toDtoList(colisRepository.findNearby(latitude, longitude, distanceKm));
    }

    @Override
    @Transactional
    public ColisDto updatePartial(UUID id, ColisRequestDto dto) {
        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé avec l'ID : " + id));

        if (dto.getNomDestinataire() != null) {
            colis.setNomDestinataire(dto.getNomDestinataire());
        }
        if (dto.getAdresseDestinataire() != null) {
            colis.setAdresseDestinataire(dto.getAdresseDestinataire());
        }
        if (dto.getTelephoneDestinataire() != null) {
            colis.setTelephoneDestinataire(dto.getTelephoneDestinataire());
        }
        if (dto.getPoids() != null) {
            colis.setPoids(dto.getPoids());
        }
        if (dto.getLongueur() != null) {
            colis.setLongueur(dto.getLongueur());
        }
        if (dto.getLargeur() != null) {
            colis.setLargeur(dto.getLargeur());
        }
        if (dto.getHauteur() != null) {
            colis.setHauteur(dto.getHauteur());
        }
        if (dto.getRemarques() != null) {
            colis.setRemarques(dto.getRemarques());
        }
        if (dto.getLatitudeDestinataire() != null) {
            colis.setLatitudeDestinataire(dto.getLatitudeDestinataire());
        }
        if (dto.getLongitudeDestinataire() != null) {
            colis.setLongitudeDestinataire(dto.getLongitudeDestinataire());
        }
        if (dto.getLatitudeCollecte() != null) {
            colis.setLatitudeCollecte(dto.getLatitudeCollecte());
        }
        if (dto.getLongitudeCollecte() != null) {
            colis.setLongitudeCollecte(dto.getLongitudeCollecte());
        }

        return colisMapper.toDto(colisRepository.save(colis));
    }

    @Override
    @Transactional
    public ColisDto assignerLivreur(UUID colisId, UUID livreurId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé avec l'ID : " + colisId));

        User livreur = userRepository.findByPublicId(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur non trouvé avec l'ID : " + livreurId));

        StatutColis ancienStatut = colis.getStatut();
        colis.setLivreur(livreur);
        colis.setStatut(StatutColis.PRIS_EN_CHARGE);

        Colis savedColis = colisRepository.save(colis);
        addHistorique(savedColis, ancienStatut, StatutColis.PRIS_EN_CHARGE, livreur, "Assignation du livreur");

        return colisMapper.toDto(savedColis);
    }

    @Override
    @Transactional
    public ColisDto collecter(UUID colisId, String commentaire) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé avec l'ID : " + colisId));

        if (colis.getLivreur() == null) {
            throw new RuntimeException("Le colis doit être assigné à un livreur avant la collecte");
        }

        StatutColis ancienStatut = colis.getStatut();
        validateStatutTransition(ancienStatut, StatutColis.COLLECTE_EFFECTUEE);

        colis.setStatut(StatutColis.COLLECTE_EFFECTUEE);
        Colis savedColis = colisRepository.save(colis);
        addHistorique(savedColis, ancienStatut, StatutColis.COLLECTE_EFFECTUEE, colis.getLivreur(), commentaire);

        return colisMapper.toDto(savedColis);
    }

    @Override
    @Transactional
    public ColisDto livrer(UUID colisId, String commentaire) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé avec l'ID : " + colisId));

        if (colis.getLivreur() == null) {
            throw new RuntimeException("Le colis doit être assigné à un livreur avant la livraison");
        }

        StatutColis ancienStatut = colis.getStatut();
        validateStatutTransition(ancienStatut, StatutColis.LIVRE);

        colis.setStatut(StatutColis.LIVRE);
        colis.setDateLivraison(LocalDateTime.now());
        Colis savedColis = colisRepository.save(colis);
        addHistorique(savedColis, ancienStatut, StatutColis.LIVRE, colis.getLivreur(), commentaire);

        return colisMapper.toDto(savedColis);
    }

    @Override
    public List<HistoriqueColisDto> getHistorique(UUID colisId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé avec l'ID : " + colisId));

        return colis.getHistorique().stream()
                .map(historiqueColisMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void annulerColis(UUID colisId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé avec l'ID : " + colisId));

        if (colis.getStatut() == StatutColis.LIVRE) {
            throw new RuntimeException("Impossible d'annuler un colis déjà livré");
        }

        StatutColis ancienStatut = colis.getStatut();
        colis.setStatut(StatutColis.ANNULE);
        colisRepository.save(colis);
        addHistorique(colis, ancienStatut, StatutColis.ANNULE, colis.getExpediteur(), "Annulation du colis");
    }

    @Override
    public String generateNumeroSuivi() {
        int year = Year.now().getValue();
        long count = colisRepository.count() + 1;
        return String.format("COL-%d-%04d", year, count);
    }

    private void addHistorique(Colis colis, StatutColis ancienStatut, StatutColis nouveauStatut, User utilisateur, String commentaire) {
        HistoriqueColis historique = new HistoriqueColis();
        historique.setColis(colis);
        historique.setAncienStatut(ancienStatut);
        historique.setNouveauStatut(nouveauStatut);
        historique.setDateChangement(LocalDateTime.now());
        historique.setUtilisateur(utilisateur);
        historique.setCommentaire(commentaire);
        historiqueColisRepository.save(historique);
    }

    private void validateStatutTransition(StatutColis ancien, StatutColis nouveau) {
        // Validation des transitions de statut
        if (nouveau == StatutColis.EN_ATTENTE_COLLECTE && ancien != null) {
            throw new RuntimeException("Impossible de revenir au statut EN_ATTENTE_COLLECTE");
        }
        if (nouveau == StatutColis.PRIS_EN_CHARGE && ancien != StatutColis.EN_ATTENTE_COLLECTE) {
            throw new RuntimeException("Le colis doit être en attente de collecte pour être pris en charge");
        }
        if (nouveau == StatutColis.COLLECTE_EFFECTUEE && ancien != StatutColis.PRIS_EN_CHARGE) {
            throw new RuntimeException("Le colis doit être pris en charge pour être collecté");
        }
        if (nouveau == StatutColis.LIVRE && ancien != StatutColis.COLLECTE_EFFECTUEE && ancien != StatutColis.EN_COURS) {
            throw new RuntimeException("Le colis doit être collecté ou en cours pour être livré");
        }
    }
}
