package com.ipnet.services.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.dto.ColisDto;
import com.ipnet.dto.ColisRequestDto;
import com.ipnet.dto.ColisStatutDto;
import com.ipnet.dto.EstimationPrixDto;
import com.ipnet.dto.HistoriqueColisDto;
import com.ipnet.entity.AgenceEntity;
import com.ipnet.entity.Colis;
import com.ipnet.entity.DemandeCollecteEntity;
import com.ipnet.entity.HistoriqueColis;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.enums.StatutColis;
import com.ipnet.enums.ModeRemise;
import com.ipnet.enums.StatutPaiementColis;
import com.ipnet.enums.TranchePoids;
import com.ipnet.exception.ColisTransitionInvalideException;
import com.ipnet.mappers.ColisMapper;
import com.ipnet.mappers.HistoriqueColisMapper;
import com.ipnet.repository.AgenceRepository;
import com.ipnet.repository.ColisRepository;
import com.ipnet.repository.DemandeCollecteRepository;
import com.ipnet.repository.HistoriqueColisRepository;
import com.ipnet.repository.TrajetRepository;
import com.ipnet.security.SecurityUtils;
import com.ipnet.security.enums.StatutCompte;
import com.ipnet.security.enums.StatutOperationnel;
import com.ipnet.security.enums.UserRole;
import com.ipnet.security.exception.ResourceNotFoundException;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.ColisServiceInterface;
import com.ipnet.services.interfaces.NotificationServiceInterface;
import com.ipnet.services.interfaces.TarifExpeditionServiceInterface;

@Service
public class ColisServiceImpl implements ColisServiceInterface {

    private final ColisRepository colisRepository;
    private final HistoriqueColisRepository historiqueColisRepository;
    private final UserRepository userRepository;
    private final AgenceRepository agenceRepository;
    private final TrajetRepository trajetRepository;
    private final TarifExpeditionServiceInterface tarifExpeditionService;
    private final ColisMapper colisMapper;
    private final HistoriqueColisMapper historiqueColisMapper;
    private final NotificationServiceInterface notificationService;
    private final DemandeCollecteRepository demandeCollecteRepository;

    public ColisServiceImpl(
            ColisRepository colisRepository,
            HistoriqueColisRepository historiqueColisRepository,
            UserRepository userRepository,
            AgenceRepository agenceRepository,
            TrajetRepository trajetRepository,
            TarifExpeditionServiceInterface tarifExpeditionService,
            ColisMapper colisMapper,
            HistoriqueColisMapper historiqueColisMapper,
            NotificationServiceInterface notificationService,
            DemandeCollecteRepository demandeCollecteRepository) {
        this.colisRepository = colisRepository;
        this.historiqueColisRepository = historiqueColisRepository;
        this.userRepository = userRepository;
        this.agenceRepository = agenceRepository;
        this.trajetRepository = trajetRepository;
        this.tarifExpeditionService = tarifExpeditionService;
        this.colisMapper = colisMapper;
        this.historiqueColisMapper = historiqueColisMapper;
        this.notificationService = notificationService;
        this.demandeCollecteRepository = demandeCollecteRepository;
    }

    @Override
    @Transactional
    public ColisDto enregistrerColis(ColisRequestDto dto) {
        if (dto.getDestinataireAdresse() == null || dto.getDestinataireAdresse().isBlank()) {
            if (dto.getModeRemise() != null && dto.getModeRemise().name().equals("LIVRAISON_DOMICILE")) {
                throw new IllegalArgumentException(
                        "L'adresse du destinataire est obligatoire pour une livraison à domicile");
            }
        }

        AgenceEntity agenceDepart = getAgence(dto.getAgenceDepartId());
        AgenceEntity agenceArrivee = getAgence(dto.getAgenceArriveeId());

        EstimationPrixDto estimation = tarifExpeditionService.estimerPrix(
                agenceDepart.getVille().getId(),
                agenceArrivee.getVille().getId(),
                dto.getTranchePoids(),
                dto.getModeRemise(),
                dto.isCollecteDomicile());

        User agent = SecurityUtils.getConnectedUser(userRepository);

        String numero = generateNumeroSuivi();
        String codeOTP = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 999999));
        String lienPublic = "http://localhost:4200/suivi/" + numero;

        Colis colis = new Colis();
        colis.setNumeroSuivi(numero);
        colis.setCodeRetrait(codeOTP);
        colis.setLienSuivi(lienPublic);
        colis.setDescription(dto.getDescription());
        colis.setTranchePoids(dto.getTranchePoids());
        colis.setDimensions(dto.getDimensions());
        colis.setModeRemise(dto.getModeRemise());
        colis.setExpediteurNom(dto.getExpediteurNom());
        colis.setExpediteurTelephone(dto.getExpediteurTelephone());
        colis.setDestinataireNom(dto.getDestinataireNom());
        colis.setDestinataireTelephone(dto.getDestinataireTelephone());
        colis.setDestinataireAdresse(dto.getDestinataireAdresse());
        colis.setAdresseCollecte(dto.getAdresseCollecte());
        colis.setLatitudeCollecte(dto.getLatitudeCollecte());
        colis.setLongitudeCollecte(dto.getLongitudeCollecte());
        colis.setAgenceDepart(agenceDepart);
        colis.setAgenceArrivee(agenceArrivee);
        colis.setAgentEnregistreur(agent);
        colis.setQrCode(lienPublic);
        colis.setPrixEstime(estimation.getTotalEstime());
        colis.setFraisCollecte(estimation.getFraisCollecte());
        colis.setFraisLivraison(estimation.getFraisLivraison());

        StatutColis initialStatut = dto.isCollecteDomicile()
                ? StatutColis.EN_ATTENTE_COLLECTE
                : StatutColis.EN_ATTENTE_DEPOT;
        colis.setStatut(initialStatut);

        Colis saved = colisRepository.save(colis);
        addHistorique(saved, null, initialStatut, agent, "Enregistrement initial du colis");

        if (dto.isCollecteDomicile()) {
            DemandeCollecteEntity demande = new DemandeCollecteEntity();
            demande.setAdresseCollecte(dto.getAdresseCollecte() != null && !dto.getAdresseCollecte().isBlank()
                    ? dto.getAdresseCollecte()
                    : "Adresse non spécifiée");
            demande.setLatitude(dto.getLatitudeCollecte());
            demande.setLongitude(dto.getLongitudeCollecte());
            demande.setDateHeureCollecte(LocalDateTime.now());
            demande.setAgence(agenceDepart);
            demande.setExpediteur(agent);
            demande.setColis(saved);
            demande.setStatut(com.ipnet.enums.StatutCollecte.EN_ATTENTE);
            demandeCollecteRepository.save(demande);
        }

        // Notification SMS/WhatsApp avec code de retrait et lien de suivi
        String agenceNom = saved.getAgenceDepart() != null ? saved.getAgenceDepart().getNom() : "TransIA";
        String msgExp = "Votre colis " + saved.getNumeroSuivi() + " a été enregistré (Agence " + agenceNom + ").\nSuivez votre colis en direct : " + saved.getLienSuivi();
        notifierParTelephone(saved.getExpediteurTelephone(), "Colis enregistré", msgExp);

        String msgDest = "Un colis vous est destiné. Référence : " + saved.getNumeroSuivi() + ".\nCode secret de retrait : " + saved.getCodeRetrait() + ".\nSuivez votre colis en direct : " + saved.getLienSuivi();
        notifierParTelephone(saved.getDestinataireTelephone(), "Nouveau colis destiné", msgDest);

        return colisMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ColisDto confirmerPeseeAjusterPrix(UUID colisId, Double poidsReel, TranchePoids trancheReelle) {
        Colis colis = getColis(colisId);
        verifierTransition(colis.getStatut(), StatutColis.EN_ATTENTE_DEPOT, "en attente de dépôt");

        colis.setPoidsReel(poidsReel);

        TranchePoids trancheFinale = trancheReelle != null ? trancheReelle : colis.getTranchePoids();
        colis.setTranchePoids(trancheFinale);

        boolean collecteDomicile = colis.getFraisCollecte() != null && colis.getFraisCollecte() > 0;
        EstimationPrixDto estimation = tarifExpeditionService.estimerPrix(
                colis.getAgenceDepart().getVille().getId(),
                colis.getAgenceArrivee().getVille().getId(),
                trancheFinale,
                colis.getModeRemise(),
                collecteDomicile);

        colis.setPrixFinal(estimation.getTotalEstime());
        colis.setStatut(StatutColis.DEPOSE_EN_AGENCE);
        colis.setStatutPaiement(StatutPaiementColis.PAYE);

        User agent = SecurityUtils.getConnectedUser(userRepository);
        Colis saved = colisRepository.save(colis);
        addHistorique(saved, StatutColis.EN_ATTENTE_DEPOT, StatutColis.DEPOSE_EN_AGENCE, agent,
                "Pesée confirmée (" + poidsReel + " kg) et prix ajusté");

        // Étape 2 — Notifications automatiques par SMS / WhatsApp
        String agenceNom = saved.getAgenceDepart() != null ? saved.getAgenceDepart().getNom() : "TransIA";
        String agenceArrNom = saved.getAgenceArrivee() != null ? saved.getAgenceArrivee().getNom() : "Destination";
        
        String msgExp = "Bonjour " + saved.getExpediteurNom() + ",\nVotre colis " + saved.getNumeroSuivi() + " est pesé et prêt à l'agence de " + agenceNom + ".\nSuivi en direct : " + saved.getLienSuivi();
        notifierParTelephone(saved.getExpediteurTelephone(), "Colis pesé et prêt", msgExp);

        String msgDest = "Bonjour " + saved.getDestinataireNom() + ",\nUn colis (Réf: " + saved.getNumeroSuivi() + ") vous est destiné depuis l'agence de " + agenceNom + ".\nCode secret OTP de retrait : " + saved.getCodeRetrait() + ".\nSuivi en direct : " + saved.getLienSuivi();
        notifierParTelephone(saved.getDestinataireTelephone(), "Colis pesé et enregistré", msgDest);

        return colisMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ColisDto chargerColisInTrajet(UUID colisId, UUID trajetId) {
        Colis colis = getColis(colisId);
        verifierTransition(colis.getStatut(), StatutColis.DEPOSE_EN_AGENCE, "déposé en agence");

        TrajetEntity trajet = trajetRepository.findById(trajetId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet introuvable"));

        colis.setTrajet(trajet);
        colis.setStatut(StatutColis.EN_TRANSIT);

        User agent = SecurityUtils.getConnectedUser(userRepository);
        Colis saved = colisRepository.save(colis);
        addHistorique(saved, StatutColis.DEPOSE_EN_AGENCE, StatutColis.EN_TRANSIT, agent,
                "Chargement dans le trajet");

        // Notification SMS / WhatsApp automatique lors du chargement dans le trajet
        String dep = trajet.getVilleDepart() != null ? trajet.getVilleDepart().getNomVille() : "Départ";
        String arr = trajet.getVilleArrivee() != null ? trajet.getVilleArrivee().getNomVille() : "Destination";
        
        String msgTransitExp = "Bonjour " + saved.getExpediteurNom() + ",\nVotre colis " + saved.getNumeroSuivi() + " a été chargé dans le car (" + dep + " → " + arr + ").\nSuivi en direct : " + saved.getLienSuivi();
        notifierParTelephone(saved.getExpediteurTelephone(), "Colis en transit", msgTransitExp);

        String msgTransitDest = "Bonjour " + saved.getDestinataireNom() + ",\nExcellente nouvelle ! Votre colis " + saved.getNumeroSuivi() + " est chargé dans le car (" + dep + " → " + arr + ").\nCode secret OTP : " + saved.getCodeRetrait() + ".\nSuivi en direct : " + saved.getLienSuivi();
        notifierParTelephone(saved.getDestinataireTelephone(), "Colis en transit", msgTransitDest);

        return colisMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ColisDto receptionnerColis(UUID colisId) {
        Colis colis = getColis(colisId);
        verifierTransition(colis.getStatut(), StatutColis.EN_TRANSIT, "en transit");

        colis.setStatut(StatutColis.ARRIVE_EN_AGENCE);

        User agent = SecurityUtils.getConnectedUser(userRepository);
        Colis saved = colisRepository.save(colis);
        addHistorique(saved, StatutColis.EN_TRANSIT, StatutColis.ARRIVE_EN_AGENCE, agent,
                "Réception à l'agence d'arrivée");

        String agenceArriveeNom = saved.getAgenceArrivee() != null ? saved.getAgenceArrivee().getNom() : "TransIA";
        
        String msgArriveeExp = "Bonjour " + saved.getExpediteurNom() + ",\nVotre colis " + saved.getNumeroSuivi() + " est bien arrivé à l'agence de destination (" + agenceArriveeNom + ").\nLe destinataire " + saved.getDestinataireNom() + " a été prévenu.";
        notifierParTelephone(saved.getExpediteurTelephone(), "Colis arrivé en agence", msgArriveeExp);

        String msgArriveeDest = "Bonjour " + saved.getDestinataireNom() + ",\nVotre colis " + saved.getNumeroSuivi() + " est ARRIVÉ à l'agence de " + agenceArriveeNom + " ! 🎉\nVenez le retirer muni de votre pièce d'identité et du Code Secret OTP : " + saved.getCodeRetrait() + "\nSuivi : " + saved.getLienSuivi();
        notifierParTelephone(saved.getDestinataireTelephone(), "Colis disponible en agence", msgArriveeDest);

        return colisMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ColisDto affecterLivreur(UUID colisId, UUID livreurId) {
        if (livreurId == null) {
            throw new IllegalArgumentException(
                    "L'identifiant du livreur est obligatoire"
            );
        }

        Colis colis = getColis(colisId);
        verifierColisAffectable(colis);

        AgenceEntity agenceArrivee = colis.getAgenceArrivee();
        UUID agenceArriveeId = agenceArrivee.getId();

        // Le SUPER_ADMIN peut agir sur toutes les agences.
        // L'ADMIN_AGENCE et l'AGENT_ACCUEIL ne peuvent agir que
        // sur les colis arrivés dans leur propre agence.
        SecurityUtils.checkAgenceAccess(
                userRepository,
                agenceArriveeId
        );

        User livreur = getLivreur(livreurId);
        verifierLivreurEligible(
                livreur,
                agenceArriveeId
        );

        // Rend l'opération idempotente : répéter la même requête
        // ne crée pas un nouvel historique inutile.
        if (estDejaAffecteAuLivreur(colis, livreurId)) {
            return colisMapper.toDto(colis);
        }

        StatutColis ancienStatut = colis.getStatut();
        User ancienLivreur = colis.getLivreur();

        colis.setLivreur(livreur);
        colis.setStatut(StatutColis.AFFECTE_AU_LIVREUR);

        User agent = SecurityUtils.getConnectedUser(userRepository);
        Colis saved = colisRepository.save(colis);

        String commentaire = construireCommentaireAffectation(
                ancienLivreur,
                livreur
        );

        addHistorique(
                saved,
                ancienStatut,
                StatutColis.AFFECTE_AU_LIVREUR,
                agent,
                commentaire
        );

        if (ancienLivreur != null
                && !ancienLivreur.getPublicId().equals(livreurId)) {
            notificationService.envoyerNotification(
                    ancienLivreur.getId(),
                    "Livraison réaffectée",
                    "Le colis " + saved.getNumeroSuivi()
                            + " a été réaffecté à un autre livreur."
            );
        }

        notificationService.envoyerNotification(
                livreur.getId(),
                "Nouvelle livraison affectée",
                "Le colis " + saved.getNumeroSuivi()
                        + " vous a été affecté pour une livraison à domicile."
        );

        return colisMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ColisDto demarrerLivraison(UUID colisId) {
        Colis colis = getColis(colisId);
        verifierTransition(
                colis.getStatut(),
                StatutColis.AFFECTE_AU_LIVREUR,
                "affecté à un livreur"
        );

        User livreurConnecte = SecurityUtils.getConnectedUser(userRepository);

        if (colis.getLivreur() == null) {
            throw new IllegalArgumentException(
                    "Aucun livreur n'est affecté à ce colis"
            );
        }

        if (!colis.getLivreur().getPublicId().equals(livreurConnecte.getPublicId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Ce colis est affecté à un autre livreur"
            );
        }

        if (livreurConnecte.getStatutOperationnel() != StatutOperationnel.DISPONIBLE
                && livreurConnecte.getStatutOperationnel() != StatutOperationnel.EN_COURSE) {
            throw new IllegalArgumentException(
                    "Votre statut opérationnel ne permet pas de démarrer cette livraison"
            );
        }

        colis.setStatut(StatutColis.EN_COURS_LIVRAISON);
        livreurConnecte.setStatutOperationnel(StatutOperationnel.EN_COURSE);

        userRepository.save(livreurConnecte);
        Colis saved = colisRepository.save(colis);

        String msgLivraisonEnCours = "Bonjour " + saved.getDestinataireNom() + ",\nVotre colis " + saved.getNumeroSuivi() + " est en cours de livraison vers votre adresse par notre livreur. 🛵\nFournissez votre Code Secret OTP (" + saved.getCodeRetrait() + ") au livreur lors de la remise.";
        notifierParTelephone(saved.getDestinataireTelephone(), "Colis en cours de livraison", msgLivraisonEnCours);

        return colisMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ColisDto confirmerLivraison(UUID colisId, String codeOtp) {
        Colis colis = getColis(colisId);
        verifierTransition(
                colis.getStatut(),
                StatutColis.EN_COURS_LIVRAISON,
                "en cours de livraison"
        );

        User livreurConnecte = SecurityUtils.getConnectedUser(userRepository);

        if (colis.getLivreur() == null
                || !colis.getLivreur().getPublicId().equals(livreurConnecte.getPublicId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Seul le livreur affecté à ce colis peut confirmer sa livraison"
            );
        }

        if (codeOtp != null && !codeOtp.trim().isEmpty()) {
            if (colis.getCodeRetrait() != null && !colis.getCodeRetrait().trim().equalsIgnoreCase(codeOtp.trim())) {
                throw new IllegalArgumentException("Code secret de retrait OTP incorrect. Remise non autorisée.");
            }
        }

        StatutColis ancien = colis.getStatut();
        colis.setStatut(StatutColis.LIVRE);
        colis.setDateLivraison(LocalDateTime.now());

        Colis saved = colisRepository.save(colis);
        addHistorique(
                saved,
                StatutColis.EN_COURS_LIVRAISON,
                StatutColis.LIVRE,
                livreurConnecte,
                "Livraison confirmée"
        );

        boolean autreLivraisonEnCours = colisRepository
                .existsByLivreur_PublicIdAndStatut(
                        livreurConnecte.getPublicId(),
                        StatutColis.EN_COURS_LIVRAISON
                );

        if (!autreLivraisonEnCours) {
            livreurConnecte.setStatutOperationnel(
                    StatutOperationnel.DISPONIBLE
            );
            userRepository.save(livreurConnecte);
        }

        String msgExp = "Bonjour " + saved.getExpediteurNom() + ",\nVotre colis " + saved.getNumeroSuivi() + " a été remis avec succès au destinataire " + saved.getDestinataireNom() + ". Merci d'utiliser TransIA !";
        String msgDest = "Bonjour " + saved.getDestinataireNom() + ",\nVotre colis " + saved.getNumeroSuivi() + " vous a été remis avec succès. Merci d'avoir choisi TransIA !";
        notifierParTelephone(saved.getExpediteurTelephone(), "Colis livré", msgExp);
        notifierParTelephone(saved.getDestinataireTelephone(), "Colis livré", msgDest);

        return colisMapper.toDto(saved);
    }

    @Override
    public ColisStatutDto getStatutColis(String numeroSuivi) {
        Colis colis = colisRepository.findByNumeroSuivi(numeroSuivi)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun colis trouvé avec le numéro de suivi : " + numeroSuivi));
        return colisMapper.toStatutDto(colis);
    }

    @Override
    public List<ColisDto> listerColisParAgence(UUID agenceId) {
        UUID cible = agenceId != null ? agenceId : SecurityUtils.getConnectedUserAgenceId(userRepository);

        if (cible == null) {
            return colisMapper.toDtoList(colisRepository.findAll());
        }

        List<Colis> resultats = new ArrayList<>(colisRepository.findByAgenceDepartId(cible));
        for (Colis colis : colisRepository.findByAgenceArriveeId(cible)) {
            if (resultats.stream().noneMatch(c -> c.getId().equals(colis.getId()))) {
                resultats.add(colis);
            }
        }

        return colisMapper.toDtoList(resultats);
    }

    @Override
    public List<ColisDto> listerColisParStatut(StatutColis statut) {
        return colisMapper.toDtoList(colisRepository.findByStatut(statut));
    }

    @Override
    public List<ColisDto> listerColisParTrajet(UUID trajetId) {
        if (trajetId == null) {
            return List.of();
        }
        return colisMapper.toDtoList(colisRepository.findByTrajetId(trajetId));
    }

    @Override
    public List<ColisDto> listerMesColis() {
        User connecte = SecurityUtils.getConnectedUser(userRepository);
        return colisMapper.toDtoList(
                colisRepository.findByExpediteurTelephoneOrderByDateCreationColisDesc(connecte.getTelephone()));
    }

    @Override
    public List<ColisDto> listerMesLivraisons() {
        User connecte = SecurityUtils.getConnectedUser(userRepository);
        return colisMapper.toDtoList(
                colisRepository.findByLivreur_PublicIdOrderByDateCreationColisDesc(connecte.getPublicId()));
    }

    @Override
    public ColisDto getById(UUID id) {
        return colisMapper.toDto(getColis(id));
    }

    @Override
    public List<HistoriqueColisDto> getHistorique(UUID colisId) {
        Colis colis = getColis(colisId);
        return colis.getHistorique().stream().map(historiqueColisMapper::toDto).toList();
    }

    @Override
    @Transactional
    public void annulerColis(UUID colisId) {
        Colis colis = getColis(colisId);

        if (colis.getStatut() == StatutColis.LIVRE) {
            throw new ColisTransitionInvalideException("Impossible d'annuler un colis déjà livré");
        }

        StatutColis ancien = colis.getStatut();
        colis.setStatut(StatutColis.ANNULE);

        User agent = SecurityUtils.getConnectedUser(userRepository);
        colisRepository.save(colis);
        addHistorique(colis, ancien, StatutColis.ANNULE, agent, "Annulation du colis");
    }

    private void verifierColisAffectable(Colis colis) {
        if (colis.getStatut() != StatutColis.ARRIVE_EN_AGENCE
                && colis.getStatut() != StatutColis.AFFECTE_AU_LIVREUR) {
            throw new ColisTransitionInvalideException(
                    "Le colis doit être arrivé à l'agence de destination "
                            + "pour être affecté à un livreur "
                            + "(statut actuel : " + colis.getStatut() + ")"
            );
        }

        if (colis.getModeRemise() != ModeRemise.LIVRAISON_DOMICILE) {
            throw new IllegalArgumentException(
                    "Ce colis est prévu pour un retrait en agence "
                            + "et ne peut pas être affecté à un livreur"
            );
        }

        if (colis.getStatutPaiement() != StatutPaiementColis.PAYE) {
            throw new IllegalArgumentException(
                    "Le paiement du colis doit être confirmé "
                            + "avant son affectation à un livreur"
            );
        }

        if (colis.getAgenceArrivee() == null
                || colis.getAgenceArrivee().getId() == null) {
            throw new IllegalArgumentException(
                    "Le colis ne possède aucune agence d'arrivée valide"
            );
        }
    }

    private User getLivreur(UUID livreurId) {
        return userRepository.findByPublicId(livreurId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Livreur introuvable avec l'identifiant : "
                                + livreurId
                ));
    }

    private void verifierLivreurEligible(
            User livreur,
            UUID agenceArriveeId
    ) {
        boolean possedeRoleLivreur = livreur.getRoles() != null
                && livreur.getRoles()
                        .stream()
                        .anyMatch(role ->
                                role.getName() == UserRole.LIVREUR
                        );

        if (!possedeRoleLivreur) {
            throw new IllegalArgumentException(
                    "L'utilisateur sélectionné ne possède pas le rôle LIVREUR"
            );
        }

        if (livreur.getStatutCompte() != StatutCompte.ACTIF) {
            throw new IllegalArgumentException(
                    "Le compte du livreur sélectionné n'est pas actif"
            );
        }

        AgenceEntity agenceLivreur = livreur.getAgence();

        if (agenceLivreur == null || agenceLivreur.getId() == null) {
            throw new IllegalArgumentException(
                    "Le livreur sélectionné n'est rattaché à aucune agence "
                            + "dans la base de données"
            );
        }

        UUID agenceLivreurId = agenceLivreur.getId();

        if (!agenceArriveeId.equals(agenceLivreurId)) {
            throw new IllegalArgumentException(
                    "Le livreur sélectionné doit appartenir à l'agence "
                            + "d'arrivée du colis. "
                            + "Agence du colis : " + agenceArriveeId
                            + " ; agence du livreur : " + agenceLivreurId
            );
        }

        if (livreur.getStatutOperationnel()
                != StatutOperationnel.DISPONIBLE) {
            throw new IllegalArgumentException(
                    "Le livreur sélectionné n'est pas disponible "
                            + "(statut actuel : "
                            + livreur.getStatutOperationnel() + ")"
            );
        }
    }

    private boolean estDejaAffecteAuLivreur(
            Colis colis,
            UUID livreurId
    ) {
        return colis.getStatut() == StatutColis.AFFECTE_AU_LIVREUR
                && colis.getLivreur() != null
                && livreurId.equals(
                        colis.getLivreur().getPublicId()
                );
    }

    private String construireCommentaireAffectation(
            User ancienLivreur,
            User nouveauLivreur
    ) {
        if (ancienLivreur == null) {
            return "Colis affecté au livreur "
                    + nouveauLivreur.getNom();
        }

        return "Colis réaffecté du livreur "
                + ancienLivreur.getNom()
                + " au livreur "
                + nouveauLivreur.getNom();
    }

    private String generateNumeroSuivi() {
        String numero;
        do {
            int suffixe = ThreadLocalRandom.current().nextInt(0, 1_000_000);
            numero = String.format("TRS-%06d", suffixe);
        } while (colisRepository.findByNumeroSuivi(numero).isPresent());
        return numero;
    }

    private void addHistorique(
            Colis colis,
            StatutColis ancien,
            StatutColis nouveau,
            User utilisateur,
            String commentaire
    ) {
        HistoriqueColis historique = new HistoriqueColis();
        historique.setColis(colis);
        historique.setAncienStatut(ancien);
        historique.setNouveauStatut(nouveau);
        historique.setDateChangement(LocalDateTime.now());
        historique.setUtilisateur(utilisateur);
        historique.setCommentaire(commentaire);
        historiqueColisRepository.save(historique);
    }

    private void verifierTransition(
            StatutColis actuel,
            StatutColis attendu,
            String libelleAttendu
    ) {
        if (actuel != attendu) {
            throw new ColisTransitionInvalideException(
                    "Le colis doit être " + libelleAttendu
                            + " pour effectuer cette action "
                            + "(statut actuel : " + actuel + ")"
            );
        }
    }

    private void notifierParTelephone(
            String telephone,
            String titre,
            String message
    ) {
        if (telephone == null || telephone.isBlank()) {
            return;
        }

        // Trace visuelle dans la console Backend Spring Boot pour la démonstration SMS / WhatsApp
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("📱 [SIMULATION ENVOI SMS / WHATSAPP]");
        System.out.println("   DESTINATAIRE : " + telephone);
        System.out.println("   SUJET        : " + titre);
        System.out.println("   CONTENU      : \n" + message);
        System.out.println("--------------------------------------------------------------------------------\n");

        userRepository.findByTelephone(telephone)
                .ifPresent(user ->
                        notificationService.envoyerNotification(
                                user.getId(),
                                titre,
                                message
                        )
                );
    }

    private Colis getColis(UUID id) {
        return colisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colis introuvable"));
    }

    private AgenceEntity getAgence(UUID id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable"));
    }
}
