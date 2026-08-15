package com.ipnet.services.implement;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.dto.DemandeCollecteDto;
import com.ipnet.dto.DemandeCollecteRequestDto;
import com.ipnet.entity.AgenceEntity;
import com.ipnet.entity.Colis;
import com.ipnet.entity.DemandeCollecteEntity;
import com.ipnet.enums.StatutCollecte;
import com.ipnet.exception.ColisTransitionInvalideException;
import com.ipnet.mappers.DemandeCollecteMapper;
import com.ipnet.repository.AgenceRepository;
import com.ipnet.repository.ColisRepository;
import com.ipnet.repository.DemandeCollecteRepository;
import com.ipnet.security.SecurityUtils;
import com.ipnet.security.exception.ResourceNotFoundException;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.DemandeCollecteServiceInterface;

@Service
public class DemandeCollecteServiceImpl implements DemandeCollecteServiceInterface {

    private final DemandeCollecteRepository demandeRepository;
    private final ColisRepository colisRepository;
    private final AgenceRepository agenceRepository;
    private final UserRepository userRepository;
    private final DemandeCollecteMapper demandeMapper;

    public DemandeCollecteServiceImpl(
            DemandeCollecteRepository demandeRepository,
            ColisRepository colisRepository,
            AgenceRepository agenceRepository,
            UserRepository userRepository,
            DemandeCollecteMapper demandeMapper) {
        this.demandeRepository = demandeRepository;
        this.colisRepository = colisRepository;
        this.agenceRepository = agenceRepository;
        this.userRepository = userRepository;
        this.demandeMapper = demandeMapper;
    }

    @Override
    @Transactional
    public DemandeCollecteDto creerDemande(DemandeCollecteRequestDto dto) {
        User expediteur = SecurityUtils.getConnectedUser(userRepository);

        AgenceEntity agence = agenceRepository.findById(dto.getAgenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable"));

        DemandeCollecteEntity demande = new DemandeCollecteEntity();
        demande.setAdresseCollecte(dto.getAdresseCollecte());
        demande.setLatitude(dto.getLatitude());
        demande.setLongitude(dto.getLongitude());
        demande.setDateHeureCollecte(dto.getDateHeureCollecte());
        demande.setExpediteur(expediteur);
        demande.setAgence(agence);

        return demandeMapper.toDto(demandeRepository.save(demande));
    }

    @Override
    @Transactional
    public DemandeCollecteDto assignerLivreur(UUID demandeId, UUID livreurId) {
        DemandeCollecteEntity demande = getDemande(demandeId);

        if (demande.getStatut() != StatutCollecte.EN_ATTENTE) {
            throw new ColisTransitionInvalideException(
                    "La demande doit être en attente pour assigner un livreur");
        }

        User livreur = userRepository.findByPublicId(livreurId)
                .orElseThrow(() -> new ResourceNotFoundException("Livreur introuvable"));

        demande.setLivreur(livreur);
        demande.setStatut(StatutCollecte.EN_COURS);

        return demandeMapper.toDto(demandeRepository.save(demande));
    }

    @Override
    @Transactional
    public DemandeCollecteDto collecterColis(UUID demandeId, UUID colisId) {
        DemandeCollecteEntity demande = getDemande(demandeId);

        if (demande.getStatut() != StatutCollecte.EN_COURS) {
            throw new ColisTransitionInvalideException(
                    "La demande doit être en cours (livreur assigné) avant la collecte");
        }

        User connecte = SecurityUtils.getConnectedUser(userRepository);
        if (demande.getLivreur() == null || !demande.getLivreur().getId().equals(connecte.getId())) {
            throw new AccessDeniedException("Cette demande n'est pas assignée à ce livreur");
        }

        if (colisId != null) {
            Colis colis = colisRepository.findById(colisId)
                    .orElseThrow(() -> new ResourceNotFoundException("Colis introuvable"));
            demande.setColis(colis);
        }

        if (demande.getColis() != null) {
            demande.getColis().setStatut(com.ipnet.enums.StatutColis.DEPOSE_EN_AGENCE);
            colisRepository.save(demande.getColis());
        }

        demande.setStatut(StatutCollecte.COLLECTE);

        return demandeMapper.toDto(demandeRepository.save(demande));
    }

    @Override
    @Transactional
    public DemandeCollecteDto annulerDemande(UUID demandeId) {
        DemandeCollecteEntity demande = getDemande(demandeId);

        if (demande.getStatut() == StatutCollecte.COLLECTE || demande.getStatut() == StatutCollecte.ANNULE) {
            throw new ColisTransitionInvalideException(
                    "Cette demande ne peut plus être annulée (statut actuel : " + demande.getStatut() + ")");
        }

        demande.setStatut(StatutCollecte.ANNULE);
        return demandeMapper.toDto(demandeRepository.save(demande));
    }

    @Override
    public List<DemandeCollecteDto> listerDemandes(UUID agenceId) {
        UUID cible = agenceId != null ? agenceId : SecurityUtils.getConnectedUserAgenceId(userRepository);

        if (cible == null) {
            return demandeMapper.toDtoList(demandeRepository.findAll());
        }

        return demandeMapper.toDtoList(demandeRepository.findByAgenceId(cible));
    }

    @Override
    public List<DemandeCollecteDto> listerDemandesLivreur(UUID livreurId) {
        return demandeMapper.toDtoList(demandeRepository.findByLivreur_PublicId(livreurId));
    }

    @Override
    public List<DemandeCollecteDto> listerMesDemandes() {
        User connecte = SecurityUtils.getConnectedUser(userRepository);
        return demandeMapper.toDtoList(demandeRepository.findByExpediteur_PublicId(connecte.getPublicId()));
    }

    private DemandeCollecteEntity getDemande(UUID id) {
        return demandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de collecte introuvable"));
    }
}
