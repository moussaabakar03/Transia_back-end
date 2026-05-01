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
import com.ipnet.enums.StatutBillet;
import com.ipnet.enums.StatutReservation;
import com.ipnet.mappers.PaiementMapper;
import com.ipnet.mappers.ReservationMapper;
import com.ipnet.mappers.VehiculeMappers;
import com.ipnet.repository.PaiementRepository;
import com.ipnet.repository.ReservationRepository;
import com.ipnet.services.interfaces.PaiementServiceInterface;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaiementServiceImpl implements PaiementServiceInterface {

    private final ReservationMapper reservationMapper;


    private ReservationRepository reservationRepository;
    private PaiementRepository paiementRepository;
    private PaiementMapper paiementMapper;
    
    

    public PaiementServiceImpl(VehiculeMappers vehiculeMappers, ReservationRepository reservationRepository,
			PaiementRepository paiementRepository, PaiementMapper paiementMapper, ReservationMapper reservationMapper) {
		super();
		this.reservationRepository = reservationRepository;
		this.paiementRepository = paiementRepository;
		this.paiementMapper = paiementMapper;
		this.reservationMapper = reservationMapper;
	}

    
	@Transactional
    public void validerPaiementCaisse(PaiementRequestDto dto) {
        Reservation res = reservationRepository.findById(dto.getReservationId().getId())
            .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        if (res.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new RuntimeException("La réservation ne peut plus être payée (Statut: " + res.getStatut() + ")");
        }

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

	@Override
	public List<PaiementRequestDto> listePaiementCaisse() {

		List<PaiementEntity> paiements = paiementRepository.findAll();
		
		ArrayList<PaiementRequestDto> paiementsDto = new ArrayList<>();
		
		for(PaiementEntity paiment : paiements)
			paiementsDto.add(paiementMapper.toDto(paiment));
		
		return paiementsDto;
	}

    @Override
    @Transactional
    public PaiementRequestDto update(PaiementRequestDto dto, UUID id) {
        PaiementEntity paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Paiement introuvable avec l'ID : " + id));

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
            .orElseThrow(() -> new RuntimeException("Paiement introuvable"));


        Reservation res = paiement.getReservation();
        if (res != null) {
            res.setStatut(StatutReservation.EN_ATTENTE);
            res.getBillets().forEach(b -> b.setStatut(StatutBillet.EN_ATTENTE)); 
            reservationRepository.save(res);
        }

        // 3. Suppression physique
        paiementRepository.delete(paiement);
    }

    @Override
    public PaiementRequestDto getPaiementCaisse(UUID id) {

    	PaiementEntity paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Paiement introuvable"));
            
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
