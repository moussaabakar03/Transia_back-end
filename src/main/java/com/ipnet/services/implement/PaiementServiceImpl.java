package com.ipnet.services.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipnet.dto.PaiementRequestDto;
import com.ipnet.dto.ReservationResponseDto;
import com.ipnet.entity.PaiementEntity;
import com.ipnet.entity.Reservation;
import com.ipnet.enums.ModePaiement;
import com.ipnet.enums.StatutBillet;
import com.ipnet.enums.StatutReservation;
import com.ipnet.exception.ReservationNonModifiableException;
import com.ipnet.mappers.PaiementMapper;
import com.ipnet.mappers.ReservationMapper;
import com.ipnet.repository.PaiementRepository;
import com.ipnet.repository.ReservationRepository;
import com.ipnet.security.SecurityUtils;
import com.ipnet.security.exception.ResourceNotFoundException;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.PaiementServiceInterface;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaiementServiceImpl implements PaiementServiceInterface {

    private final ReservationMapper reservationMapper;
    private final ReservationRepository reservationRepository;
    private final PaiementRepository paiementRepository;
    private final PaiementMapper paiementMapper;
    private final UserRepository userRepository;

    public PaiementServiceImpl(ReservationRepository reservationRepository,
            PaiementRepository paiementRepository, PaiementMapper paiementMapper, ReservationMapper reservationMapper,
            UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.paiementRepository = paiementRepository;
        this.paiementMapper = paiementMapper;
        this.reservationMapper = reservationMapper;
        this.userRepository = userRepository;
    }

    /** Paiement encaissé physiquement par le staff au comptoir (espèces). */
    @Transactional
    public void validerPaiementCaisse(PaiementRequestDto dto) {
        Reservation res = reservationRepository.findById(dto.getReservationId())
            .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable"));

        checkAgenceAccessSurReservation(res);

        enregistrerPaiementEtConfirmer(res, dto, "CAISSE");
    }

    /**
     * Paiement effectué par le client lui-même depuis l'app mobile (TMONEY/FLOOZ/carte).
     * Aucune intégration réelle d'opérateur mobile money pour l'instant (nécessiterait un compte
     * marchand) : le paiement est enregistré et confirmé immédiatement côté serveur, comme le ferait
     * un webhook de confirmation d'un vrai fournisseur — suffisant pour la démonstration/soutenance.
     */
    @Transactional
    public ReservationResponseDto payerEnLigne(PaiementRequestDto dto) {
        Reservation res = reservationRepository.findById(dto.getReservationId())
            .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable"));

        User caller = SecurityUtils.getConnectedUser(userRepository);
        if (res.getUser() == null || !res.getUser().getPublicId().equals(caller.getPublicId())) {
            throw new AccessDeniedException("Vous ne pouvez payer que vos propres réservations");
        }

        if (dto.getModePaiement() == null || dto.getModePaiement() == ModePaiement.ESPECES) {
            throw new IllegalArgumentException("Le paiement en ligne ne peut pas être en espèces");
        }

        enregistrerPaiementEtConfirmer(res, dto, "EN-LIGNE");
        return reservationMapper.toDto(res);
    }

    private void enregistrerPaiementEtConfirmer(Reservation res, PaiementRequestDto dto, String prefixeReference) {
        if (res.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new ReservationNonModifiableException(
                    "La réservation ne peut plus être payée (statut : " + res.getStatut() + ")");
        }

        Double montantAttendu = res.getTrajet().getTarif() * res.getNombrePlace();

        if (dto.getMontantVerse() == null || dto.getMontantVerse() < montantAttendu) {
            throw new IllegalArgumentException("Montant insuffisant. Attendu : " + montantAttendu);
        }

        PaiementEntity p = new PaiementEntity();
        p.setReservation(res);
        p.setMontant(dto.getMontantVerse());
        p.setReference(dto.getReference() != null && !dto.getReference().isBlank()
                ? dto.getReference()
                : prefixeReference + "-" + System.currentTimeMillis());
        p.setDatePaiement(LocalDateTime.now());
        p.setModePaiement(dto.getModePaiement());
        paiementRepository.save(p);

        res.setStatut(StatutReservation.CONFIRMEE);
        res.getBillets().forEach(b -> b.setStatut(StatutBillet.VALIDE));

        reservationRepository.save(res);
    }

    /** SUPER_ADMIN voit tout ; ADMIN_AGENCE/AGENT_ACCUEIL restreints aux réservations de leur agence. */
    private void checkAgenceAccessSurReservation(Reservation res) {
        UUID agenceId = res.getTrajet() != null && res.getTrajet().getAgence() != null
                ? res.getTrajet().getAgence().getId()
                : null;
        SecurityUtils.checkAgenceAccess(userRepository, agenceId);
    }

    @Override
    public List<PaiementRequestDto> listePaiementCaisse() {
        List<PaiementEntity> paiements = paiementRepository.findAll();

        ArrayList<PaiementRequestDto> paiementsDto = new ArrayList<>();

        for (PaiementEntity paiement : paiements) {
            if (!peutAccederAgence(paiement)) continue;
            paiementsDto.add(paiementMapper.toDto(paiement));
        }

        return paiementsDto;
    }

    private boolean peutAccederAgence(PaiementEntity paiement) {
        if (SecurityUtils.hasRole("SUPER_ADMIN")) return true;
        Reservation res = paiement.getReservation();
        UUID agenceId = res != null && res.getTrajet() != null && res.getTrajet().getAgence() != null
                ? res.getTrajet().getAgence().getId()
                : null;
        UUID callerAgenceId = SecurityUtils.getConnectedUserAgenceId(userRepository);
        return agenceId != null && agenceId.equals(callerAgenceId);
    }

    @Override
    @Transactional
    public PaiementRequestDto update(PaiementRequestDto dto, UUID id) {
        PaiementEntity paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paiement introuvable avec l'ID : " + id));

        if (paiement.getReservation() != null) {
            checkAgenceAccessSurReservation(paiement.getReservation());
        }

        paiement.setReference(dto.getReference());
        paiement.setModePaiement(dto.getModePaiement());
        paiement.setMontant(dto.getMontantVerse());

        PaiementEntity updated = paiementRepository.save(paiement);
        return paiementMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        PaiementEntity paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paiement introuvable"));

        Reservation res = paiement.getReservation();
        if (res != null) {
            res.setStatut(StatutReservation.EN_ATTENTE);
            res.getBillets().forEach(b -> b.setStatut(StatutBillet.EN_ATTENTE));
            reservationRepository.save(res);
        }

        paiementRepository.delete(paiement);
    }

    @Override
    public PaiementRequestDto getPaiementCaisse(UUID id) {
        PaiementEntity paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paiement introuvable"));

        if (paiement.getReservation() != null) {
            checkAgenceAccessSurReservation(paiement.getReservation());
        }

        return paiementMapper.toDto(paiement);
    }

    @Override
    public List<ReservationResponseDto> getReservationsByTrajet(UUID trajetId) {
        return reservationRepository.findByTrajetId(trajetId)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

}
