package com.ipnet.services.interfaces;

import com.ipnet.dto.ReservationRequestDto;
import com.ipnet.dto.ReservationResponseDto;

public interface ReservationServiceInterface {
    ReservationResponseDto create(ReservationRequestDto dto);
    ReservationResponseDto getById(Long id);
}