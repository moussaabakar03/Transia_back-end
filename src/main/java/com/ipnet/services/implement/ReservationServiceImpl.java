package com.ipnet.services.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

    private ReservationRepository reservationRepository;
    private BilletRepository billetRepository;
    private UserRepository userRepository;
    private TrajetRepository trajetRepository;
    private ReservationMapper reservationMapper;

    
    public ReservationServiceImpl(ReservationRepository reservationRepository, BilletRepository billetRepository,
			UserRepository userRepository, TrajetRepository trajetRepository, ReservationMapper reservationMapper) {
		super();
		this.reservationRepository = reservationRepository;
		this.billetRepository = billetRepository;
		this.userRepository = userRepository;
		this.trajetRepository = trajetRepository;
		this.reservationMapper = reservationMapper;
	}

	@Transactional
    public ReservationResponseDto create(ReservationRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        TrajetEntity trajet = trajetRepository.findById(dto.getTrajetId())
            .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

        Reservation res = new Reservation();
        res.setDateReservation(LocalDateTime.now());
        res.setExpiration(LocalDateTime.now().plusHours(36));
        res.setNombrePlace(dto.getNombrePlace());
        res.setStatut(StatutReservation.EN_ATTENTE);
        res.setUser(user);
        res.setTrajet(trajet);
        res.setNomResponsable(dto.getNomResponsable());
        
        Reservation savedRes = reservationRepository.save(res);

        List<BilletEntity> billets = new ArrayList<>();
        for (int i = 0; i < dto.getNombrePlace(); i++) {
            BilletEntity billet = new BilletEntity();
            billet.setReservation(savedRes);
            billet.setStatut(StatutBillet.VALIDE);
            
            if (i == 0) {
                billet.setNomPassager(dto.getNomResponsable());
            } else if (dto.getNomsPassagers() != null && i <= dto.getNomsPassagers().size()) {
                billet.setNomPassager(dto.getNomsPassagers().get(i-1));
            } else {
                billet.setNomPassager("Passager de " + dto.getNomResponsable());
            }
            billet.setQrCode(UUID.randomUUID().toString());
            billets.add(billet);
        }
        billetRepository.saveAll(billets);
        
        return reservationMapper.toDto(savedRes);
    }

    public ReservationResponseDto getById(Long id) {
        Reservation res = reservationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        return reservationMapper.toDto(res);
    }
}