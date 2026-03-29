package com.ipnet.services.implement;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipnet.dto.PaiementRequestDto;
import com.ipnet.entity.PaiementEntity;
import com.ipnet.entity.Reservation;
import com.ipnet.enums.StatutBillet;
import com.ipnet.enums.StatutReservation;
import com.ipnet.repository.PaiementRepository;
import com.ipnet.repository.ReservationRepository;
import com.ipnet.services.interfaces.PaiementServiceInterface;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaiementServiceImpl implements PaiementServiceInterface {

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private PaiementRepository paiementRepository;

    @Transactional
    public void validerPaiementCaisse(PaiementRequestDto dto) {
        //Trouver la réservation
        Reservation res = reservationRepository.findById(dto.getReservationId())
            .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        //Vérifier si elle n'est pas déjà payée ou expirée
        if (res.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new RuntimeException("La réservation ne peut plus être payée (Statut: " + res.getStatut() + ")");
        }

        //Calculer le montant attendu (Prix trajet * nombre places)
        Double montantAttendu = res.getTrajet().getTarif() * res.getNombrePlace();
        
        //Vérification de sécurité sur le montant
        if (dto.getMontantVerse() < montantAttendu) {
            throw new RuntimeException("Montant insuffisant. Attendu: " + montantAttendu);
        }

        // Enregistrer le paiement
        PaiementEntity p = new PaiementEntity();
        p.setReservation(res);
        p.setMontant(dto.getMontantVerse());
        p.setReference(dto.getReference());
        p.setDatePaiement(LocalDateTime.now());
        p.setModePaiement(dto.getModePaiement());
        paiementRepository.save(p);

        res.setStatut(StatutReservation.CONFIRMEE);
        
        res.getBillets().forEach(b -> b.setStatut(StatutBillet.VALIDE));
        
        reservationRepository.save(res);
    }
}