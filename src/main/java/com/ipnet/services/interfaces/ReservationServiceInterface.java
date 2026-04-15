package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.ReservationRequestDto;
import com.ipnet.dto.ReservationResponseDto;

public interface ReservationServiceInterface {
    ReservationResponseDto create(ReservationRequestDto dto);
    ReservationResponseDto getById(UUID id);
	Integer nombrePlaceTrajet(UUID trajetId);
	List<ReservationResponseDto> listeReservations();
	void annulerReservation(UUID id);
	ReservationResponseDto modifierReservation(UUID id, ReservationRequestDto dto);
}