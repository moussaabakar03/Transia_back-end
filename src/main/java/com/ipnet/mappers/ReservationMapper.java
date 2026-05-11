package com.ipnet.mappers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipnet.dto.ReservationResponseDto;
import com.ipnet.entity.Reservation;
import com.ipnet.security.mappers.UserMapper;

@Component
public class ReservationMapper {

    @Autowired
    private BilletMapper billetMapper;

    @Autowired(required = false)   // S’ils n’existent pas encore, on les mettra en place
    private UserMapper userMapper;

    @Autowired(required = false)
    private PaiementMapper paiementMapper;

    public ReservationResponseDto toDto(Reservation entity) {
        ReservationResponseDto dto = new ReservationResponseDto();
        dto.setId(entity.getId());
        dto.setStatut(entity.getStatut());
        dto.setNombrePlace(entity.getNombrePlace());
        dto.setDateReservation(entity.getDateReservation());
        dto.setTrajetId(entity.getTrajet().getId());
        dto.setNomResponsable(entity.getNomResponsable());
        dto.setTypeReservation(entity.getTypeReservation());

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