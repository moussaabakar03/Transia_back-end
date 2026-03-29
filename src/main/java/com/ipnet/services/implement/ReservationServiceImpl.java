package com.ipnet.services.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.dto.ReservationRequestDto;
import com.ipnet.dto.ReservationResponseDto;
import com.ipnet.entity.*;
import com.ipnet.enums.*;
import com.ipnet.mappers.ReservationMapper;
import com.ipnet.repository.*;
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

    @Override
    @Transactional
    public ReservationResponseDto create(ReservationRequestDto dto) {
        // 1. Vérifications Entités
        User user = (dto.getUserId() != null) ? 
            userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé")) : null;
        
        TrajetEntity trajet = trajetRepository.findById(dto.getTrajetId())
            .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

        // 2. Gestion des places disponibles (Filtrage par statut via Repository)
        Integer dejaOccupe = reservationRepository.sumPlacesOccupéesByTrajetId(dto.getTrajetId());
        if (dejaOccupe == null) dejaOccupe = 0;

        int capaciteTotale = trajet.getVehicule().getCapacite();
        int placesRestantes = capaciteTotale - dejaOccupe;

        if (dto.getNombrePlace() > placesRestantes) {
            throw new RuntimeException("Désolé, il ne reste que " + placesRestantes + " places disponibles.");
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
        
        Reservation savedRes = reservationRepository.save(res);

        // 4. Génération Billets
       
    	List<BilletEntity> billets = new ArrayList<>();

        for (int i = 0; i < dto.getNombrePlace(); i++) {
            BilletEntity billet = new BilletEntity();
            billet.setReservation(savedRes);

            // 1. Déterminer le nom du passager

            String nomPassager = (i == 0) ? dto.getNomResponsable() : 
                (dto.getNomsPassagers() != null && i - 1 < dto.getNomsPassagers().size()) ? 
                dto.getNomsPassagers().get(i - 1) : "Invité de " + dto.getNomResponsable();

            billet.setNomPassager(nomPassager);

            billet.setStatut(StatutBillet.EN_ATTENTE);


            String infoQr = String.format("ID:%s|NOM:%s|TRAJET:%s|DATE:%s|STATUT:%s",
                UUID.randomUUID().toString().substring(0, 8), 
                nomPassager,
                savedRes.getTrajet().getVilleDepart().getNomVille() + "-" + savedRes.getTrajet().getVilleArrivee().getNomVille(),
                savedRes.getTrajet().getHeureDepart().toString(),
                billet.getStatut()

            );

            billet.setQrCode(infoQr); 
            billets.add(billet);
        }

        billetRepository.saveAll(billets);        
        return reservationMapper.toDto(savedRes);
    }
    

    @Override
    @Transactional
    public void annulerReservation(Long id) {
        Reservation res = reservationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Réservation introuvable"));
        
        // Seules les réservations non encore terminées peuvent être annulées
        if (res.getStatut() == StatutReservation.EN_ATTENTE || res.getStatut() == StatutReservation.CONFIRMEE) {
            res.setStatut(StatutReservation.ANNULEE);
            // On invalide les billets associés
            if(res.getBillets() != null) {
                res.getBillets().forEach(b -> b.setStatut(StatutBillet.ANNULE));
            }
            reservationRepository.save(res);
        } else {
            throw new RuntimeException("Impossible d'annuler une réservation déjà " + res.getStatut());
        }
    }

    @Override
    @Transactional
    public ReservationResponseDto getById(Long id) {
        Reservation res = reservationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        
        // Logique d'expiration à la volée : si on consulte et que le temps est dépassé
        if (res.getStatut() == StatutReservation.EN_ATTENTE && LocalDateTime.now().isAfter(res.getExpiration())) {
            res.setStatut(StatutReservation.EXPIREE);
            reservationRepository.save(res);
        }
        
        return reservationMapper.toDto(res);
    }

    @Override
    public Integer nombrePlaceTrajet(Long trajetId) {
        Integer total = reservationRepository.sumPlacesOccupéesByTrajetId(trajetId);
        return (total != null) ? total : 0;
    }

    @Override
    public List<ReservationResponseDto> listeReservations() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }
    
    
    
    @Override
    @Transactional
    public ReservationResponseDto modifierReservation(Long id, ReservationRequestDto dto) {
        Reservation res = reservationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Réservation introuvable avec l'ID : " + id));

        if (res.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new RuntimeException("Modification impossible : La réservation est déjà " + res.getStatut());
        }

        Integer occupeTotal = reservationRepository.sumPlacesOccupéesByTrajetId(res.getTrajet().getId());
        int dejaOccupe = (occupeTotal != null) ? occupeTotal : 0;
        
        int placesDisponibles = res.getTrajet().getVehicule().getCapacite() - (dejaOccupe - res.getNombrePlace());

        if (dto.getNombrePlace() > placesDisponibles) {
            throw new RuntimeException("Places insuffisantes. Il ne reste que " + placesDisponibles + " places.");
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
            billet.setStatut(StatutBillet.VALIDE);

            String nomPassager;
            if (i == 0) {
                nomPassager = res.getNomResponsable(); // Utilise le nouveau nom mis à jour au point 
            } else if (dto.getNomsPassagers() != null && i - 1 < dto.getNomsPassagers().size()) {
                nomPassager = dto.getNomsPassagers().get(i - 1);
            } else {
                nomPassager = "Invité de " + res.getNomResponsable();
            }

            billet.setNomPassager(nomPassager);
            
            String infoQr = String.format("ID:%s|NOM:%s|TRAJET:%s|DATE:%s|STATUT:%s",
                    UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    nomPassager,
                    res.getTrajet().getVilleDepart().getNomVille() + "-" + res.getTrajet().getVilleArrivee().getNomVille(),
                    res.getTrajet().getHeureDepart().toString(),
                    billet.getStatut()
                );

            billet.setQrCode(infoQr); 
            nouveauxBillets.add(billet);
        }

        billetRepository.saveAll(nouveauxBillets);
        res.setBillets(nouveauxBillets); 

        return reservationMapper.toDto(reservationRepository.save(res));
    }
    
    
}