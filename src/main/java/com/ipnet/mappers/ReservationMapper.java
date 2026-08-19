package com.ipnet.mappers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipnet.dto.ReservationResponseDto;
import com.ipnet.entity.Reservation;

@Component
public class ReservationMapper {

    @Autowired
    private BilletMapper billetMapper;

    @Autowired
    private TrajetMapper trajetMapper;

    @Autowired
    private PaiementMapper paiementMapper;

    public ReservationResponseDto toDto(Reservation entity) {
        ReservationResponseDto dto = new ReservationResponseDto();
        dto.setId(entity.getId());
        dto.setStatut(entity.getStatut());
        dto.setNombrePlace(entity.getNombrePlace());
        dto.setDateReservation(entity.getDateReservation());
        dto.setNomResponsable(entity.getNomResponsable());
        dto.setTypeReservation(entity.getTypeReservation());

        if (entity.getReference() != null && !entity.getReference().isBlank()) {
            dto.setReference(entity.getReference());
        } else if (entity.getId() != null) {
            dto.setReference("RES-" + entity.getId().toString().substring(0, 8).toUpperCase());
        }

        if (entity.getTrajet() != null) {
            dto.setTrajetId(entity.getTrajet().getId());
            dto.setTrajet(trajetMapper.toResponse(entity.getTrajet()));
        }

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getPublicId());
        }

        if (entity.getPaiement() != null) {
            dto.setPaiement(paiementMapper.toDto(entity.getPaiement()));
        }

        // Conversion des billets
        if (entity.getBillets() != null) {
            dto.setBillets(entity.getBillets().stream()
                    .map(billetMapper::toDto)
                    .toList());
        } else {
            dto.setBillets(List.of());
        }


        return dto;
    }
}
