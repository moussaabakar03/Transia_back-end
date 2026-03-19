package com.ipnet.services.interfaces;

import java.util.List;

import com.ipnet.dto.ReservationRequestDto;
import com.ipnet.dto.ReservationResponseDto;

public interface ReservationServiceInterface {
    ReservationResponseDto create(ReservationRequestDto dto);
    ReservationResponseDto getById(Long id);
	Integer nombrePlaceTrajet(Long trajetId);
	List<ReservationResponseDto> listeReservations();
	void annulerReservation(Long id);
	ReservationResponseDto modifierReservation(Long id, ReservationRequestDto dto);
}