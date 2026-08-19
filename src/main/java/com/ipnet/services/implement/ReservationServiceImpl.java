package com.ipnet.services.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.dto.ReservationRequestDto;
import com.ipnet.dto.ReservationResponseDto;
import com.ipnet.entity.*;
import com.ipnet.enums.*;
import com.ipnet.exception.PlacesInsuffisantesException;
import com.ipnet.exception.ReservationNonModifiableException;
import com.ipnet.exception.SiegeIndisponibleException;
import com.ipnet.exception.TrajetIntrouvableException;
import com.ipnet.mappers.ReservationMapper;
import com.ipnet.repository.*;
import com.ipnet.security.SecurityUtils;
import com.ipnet.security.exception.ResourceNotFoundException;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.ReservationServiceInterface;

@Service
public class ReservationServiceImpl implements ReservationServiceInterface {

    private final ReservationRepository reservationRepository;
    private final BilletRepository billetRepository;
    private final UserRepository userRepository;
    private final TrajetRepository trajetRepository;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository, BilletRepository billetRepository,
            UserRepository userRepository, TrajetRepository trajetRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.billetRepository = billetRepository;
        this.userRepository = userRepository;
        this.trajetRepository = trajetRepository;
        this.reservationMapper = reservationMapper;
    }

    /**
     * L'identité du titulaire n'est jamais acceptée depuis le client : un client connecté (rôle CLIENT)
     * est automatiquement rattaché à sa propre réservation via son JWT. Le staff (SUPER_ADMIN/ADMIN_AGENCE/
     * AGENT_ACCUEIL) peut créer une réservation "au comptoir" pour un client de passage sans compte :
     * dans ce cas `user` reste null, seul `nomResponsable` identifie la réservation.
     */
    private User resolveTitulaire() {
        if (SecurityUtils.hasRole("CLIENT")) {
            return SecurityUtils.getConnectedUser(userRepository);
        }
        return null;
    }

    @Override
    @Transactional
    public ReservationResponseDto create(ReservationRequestDto dto) {
        // 1. Vérifications Entités
        User user = resolveTitulaire();

        // Verrou pessimiste sur le trajet : évite qu'une requête concurrente ne réserve les mêmes
        // places restantes avant que celle-ci ne soit validée et enregistrée.
        TrajetEntity trajet = trajetRepository.findByIdForUpdate(dto.getTrajetId())
            .orElseThrow(TrajetIntrouvableException::new);

        // 2. Gestion des places disponibles (Filtrage par statut via Repository)
        Integer dejaOccupe = reservationRepository.sumPlacesOccupéesByTrajetId(dto.getTrajetId());
        if (dejaOccupe == null) dejaOccupe = 0;

        int capaciteTotale = trajet.getVehicule().getCapacite();
        int placesRestantes = capaciteTotale - dejaOccupe;

        if (dto.getNombrePlace() > placesRestantes) {
            throw new PlacesInsuffisantesException("Désolé, il ne reste que " + placesRestantes + " places disponibles.");
        }

        // 3. Création Reservation
        Reservation res = new Reservation();
        res.setDateReservation(LocalDateTime.now());
        res.setExpiration(LocalDateTime.now().plusHours(36));
        res.setStatut(StatutReservation.EN_ATTENTE);
        res.setUser(user);
        res.setTrajet(trajet);
        res.setNombrePlace(dto.getNombrePlace());
        res.setNomResponsable(dto.getNomResponsable());
        // Dérivé du rôle du JWT, jamais du client : un CLIENT réserve forcément en ligne, le staff
        // saisit forcément au comptoir. L'app mobile n'envoie d'ailleurs jamais ce champ.
        res.setTypeReservation(SecurityUtils.hasRole("CLIENT") ? TypeReservation.EN_LIGNE : TypeReservation.PRESENTIEL);
        String refCode = "RES-" + String.format("%06d", (int) (Math.random() * 900000 + 100000));
        res.setReference(refCode);

        Reservation savedRes = reservationRepository.save(res);

        // 4. Création des billets
        List<BilletEntity> billets = new ArrayList<>();

        for (int i = 0; i < dto.getNombrePlace(); i++) {
            BilletEntity billet = new BilletEntity();
            billet.setReservation(savedRes);
            String nomPassager = (i == 0) ? dto.getNomResponsable() :
                (dto.getNomsPassagers() != null && i - 1 < dto.getNomsPassagers().size()) ?
                dto.getNomsPassagers().get(i - 1) : "Invité " + i + " de " + dto.getNomResponsable();
            billet.setNomPassager(nomPassager);
            billet.setStatut(StatutBillet.EN_ATTENTE);
            billets.add(billet);
        }

        // 4.5 Attribution des sièges AVANT génération du QR code
        assignerSieges(billets, dto.getSiegesChoisis(), trajet, false, null);

        // 4.6 QR code généré APRÈS attribution des sièges (pour inclure le numéro correct)
        for (BilletEntity billet : billets) {
            String infoQr = String.format("ID:%s|SIEGE:%s|NOM:%s|TRAJET:%s|DATE:%s|STATUT:%s",
                UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                billet.getNumeroSiege(),
                billet.getNomPassager(),
                savedRes.getTrajet().getVilleDepart().getNomVille() + "-" + savedRes.getTrajet().getVilleArrivee().getNomVille(),
                savedRes.getTrajet().getHeureDepart().toString(),
                billet.getStatut()
            );
            billet.setQrCode(infoQr);
        }

        billetRepository.saveAll(billets);
        return reservationMapper.toDto(savedRes);
    }
    

    @Override
    @Transactional
    public void annulerReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable."));

        if (reservation.getStatut() != StatutReservation.EN_ATTENTE
                && reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new ReservationNonModifiableException(
                    "Cette réservation ne peut plus être annulée (statut : " + reservation.getStatut() + ")."
            );
        }

        reservation.setStatut(StatutReservation.ANNULEE);

        if (reservation.getBillets() != null) {
            reservation.getBillets().forEach(billet -> billet.setStatut(StatutBillet.ANNULE));
        }

        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public ReservationResponseDto getById(UUID id) {
        Reservation res = reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        // Logique d'expiration à la volée : si on consulte et que le temps est dépassé
        if (res.getStatut() == StatutReservation.EN_ATTENTE && LocalDateTime.now().isAfter(res.getExpiration())) {
            res.setStatut(StatutReservation.EXPIREE);
            reservationRepository.save(res);
        }
        
        return reservationMapper.toDto(res);
    }

    @Override
    public Integer nombrePlaceTrajet(UUID trajetId) {
        Integer total = reservationRepository.sumPlacesOccupéesByTrajetId(trajetId);
        return (total != null) ? total : 0;
    }

    @Override
    public List<ReservationResponseDto> listeReservations() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    /** Réservations du client connecté, résolues via son JWT — remplace le filtrage côté mobile
     * (par numericUserId/nom, peu fiable) qui existait faute d'un tel endpoint scopé côté serveur. */
    @Override
    public List<ReservationResponseDto> mesReservations() {
        User caller = SecurityUtils.getConnectedUser(userRepository);
        return reservationRepository.findByUser_PublicId(caller.getPublicId()).stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }
    
    
    
    @Override
    @Transactional
    public ReservationResponseDto modifierReservation(UUID id, ReservationRequestDto dto) {
        Reservation res = reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable avec l'ID : " + id));

        if (res.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new ReservationNonModifiableException("Modification impossible : la réservation est déjà " + res.getStatut());
        }

        // Verrou pessimiste sur le trajet pour la même raison qu'à la création : empêche une
        // survente si une autre réservation est créée/modifiée en même temps sur ce trajet.
        TrajetEntity trajet = trajetRepository.findByIdForUpdate(res.getTrajet().getId())
            .orElseThrow(TrajetIntrouvableException::new);

        Integer occupeTotal = reservationRepository.sumPlacesOccupéesByTrajetId(trajet.getId());
        int dejaOccupe = (occupeTotal != null) ? occupeTotal : 0;

        int placesDisponibles = trajet.getVehicule().getCapacite() - (dejaOccupe - res.getNombrePlace());

        if (dto.getNombrePlace() > placesDisponibles) {
            throw new PlacesInsuffisantesException("Places insuffisantes. Il ne reste que " + placesDisponibles + " places.");
        }

        res.setNombrePlace(dto.getNombrePlace());
        res.setNomResponsable(dto.getNomResponsable());

        // 5. Nettoyage des anciens billets
        if (res.getBillets() != null) {
            billetRepository.deleteAll(res.getBillets());
            res.getBillets().clear();
        }

        List<BilletEntity> nouveauxBillets = new ArrayList<>();

        for (int i = 0; i < dto.getNombrePlace(); i++) {
            BilletEntity billet = new BilletEntity();
            billet.setReservation(res);
            // Cohérent avec creerTrajet() : un billet ne devient VALIDE qu'après paiement effectif
            // (validerPaiementCaisse / paiement en ligne), jamais directement à la modification.
            billet.setStatut(StatutBillet.EN_ATTENTE);
            String nomPassager = (i == 0) ? res.getNomResponsable() :
                (dto.getNomsPassagers() != null && i - 1 < dto.getNomsPassagers().size()) ?
                dto.getNomsPassagers().get(i - 1) : "Invité de " + res.getNomResponsable();
            billet.setNomPassager(nomPassager);
            nouveauxBillets.add(billet);
        }

        // Attribution des sièges AVANT génération du QR code
        assignerSieges(nouveauxBillets, dto.getSiegesChoisis(), res.getTrajet(), true, res);

        // QR code généré APRÈS attribution des sièges
        for (BilletEntity billet : nouveauxBillets) {
            String infoQr = String.format("ID:%s|SIEGE:%s|NOM:%s|TRAJET:%s|DATE:%s|STATUT:%s",
                UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                billet.getNumeroSiege(),
                billet.getNomPassager(),
                res.getTrajet().getVilleDepart().getNomVille() + "-" + res.getTrajet().getVilleArrivee().getNomVille(),
                res.getTrajet().getHeureDepart().toString(),
                billet.getStatut()
            );
            billet.setQrCode(infoQr);
        }
        
        billetRepository.saveAll(nouveauxBillets);
        res.setBillets(nouveauxBillets); 

        return reservationMapper.toDto(reservationRepository.save(res));
    }
    
    @Override
    public List<ReservationResponseDto> getReservationsByTrajet(UUID trajetId) {
        return reservationRepository.findByTrajetId(trajetId)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }
    
    
    private void assignerSieges(List<BilletEntity> billets, List<String> siegesChoisis,
            TrajetEntity trajet, boolean isModification, Reservation existingRes) {
		int capacite = trajet.getVehicule().getCapacite();
		
		// Récupérer tous les sièges déjà occupés pour ce trajet
		List<String> occupes = billetRepository.findOccupiedSeatsByTrajetId(trajet.getId());
		if (isModification && existingRes != null && existingRes.getBillets() != null) {
			// En modification, on retire les sièges de l'ancienne réservation
			existingRes.getBillets().stream()
			.map(BilletEntity::getNumeroSiege)
			.filter(Objects::nonNull)
			.forEach(occupes::remove);
		}
		
		// Ensemble des sièges déjà attribués dans la même réservation (pour éviter les doublons)
		Set<String> alreadyAssigned = new HashSet<>();
		
		// Attribution des sièges choisis par l'utilisateur
		if (siegesChoisis != null && !siegesChoisis.isEmpty()) {
			for (int i = 0; i < billets.size() && i < siegesChoisis.size(); i++) {
				String siege = siegesChoisis.get(i);
				// Validation basique : siège numérique entre 1 et la capacité
				try {
					int num = Integer.parseInt(siege);
					if (num < 1 || num > capacite) {
					   throw new SiegeIndisponibleException("Siège invalide : " + siege);
					}
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Format de siège invalide : " + siege);
				}
				if (occupes.contains(siege) || alreadyAssigned.contains(siege)) {
					throw new SiegeIndisponibleException("Le siège " + siege + " est déjà occupé.");
				}
				billets.get(i).setNumeroSiege(siege);
				alreadyAssigned.add(siege);
			}
		}
		
		// Pour les billets restants (sans siège), attribution aléatoire automatique
		for (BilletEntity billet : billets) {
			if (billet.getNumeroSiege() != null) continue; // déjà traité
			
			// Collecter les sièges libres (non occupés et non déjà attribués dans cette réservation)
			List<Integer> disponibles = new ArrayList<>();
			for (int i = 1; i <= capacite; i++) {
				String siegeCandidate = String.valueOf(i);
				if (!occupes.contains(siegeCandidate) && !alreadyAssigned.contains(siegeCandidate)) {
					disponibles.add(i);
				}
			}
			if (disponibles.isEmpty()) {
				throw new SiegeIndisponibleException("Aucun siège disponible pour ce trajet.");
			}
			// Choisir aléatoirement un siège parmi les disponibles
			int randomIndex = (int) (Math.random() * disponibles.size());
			String siegeAttribue = String.valueOf(disponibles.get(randomIndex));
			billet.setNumeroSiege(siegeAttribue);
			alreadyAssigned.add(siegeAttribue);
		}
	}
    
    
    @Override
    public List<String> getOccupiedSeats(UUID trajetId) {
        return billetRepository.findOccupiedSeatsByTrajetId(trajetId);
    }
    
    
}