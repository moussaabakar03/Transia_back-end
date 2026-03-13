package com.ipnet.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipnet.dto.ReservationResponseDto;
import com.ipnet.entity.Reservation;

@Component
public class ReservationMapper {

    @Autowired
    private BilletMapper billetMapper; // Il faudra aussi créer ce petit mapper

    public ReservationResponseDto toDto(Reservation entity) {
        ReservationResponseDto dto = new ReservationResponseDto();
        dto.setId(entity.getId());
        dto.setStatut(entity.getStatut());
        dto.setNombrePlace(entity.getNombrePlace());
        dto.setDateReservation(entity.getDateReservation());
        dto.setTrajetId(entity.getTrajet().getId());
        
        // Conversion de la liste des billets
        if (entity.getBillets() != null) {
            dto.setBillets(entity.getBillets().stream()
                    .map(billetMapper::toDto)
                    .toList());
        }
        return dto;
    }
}