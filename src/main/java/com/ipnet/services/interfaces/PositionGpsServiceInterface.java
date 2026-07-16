package com.ipnet.services.interfaces;

import com.ipnet.dto.PositionGpsDto;

import java.util.List;

public interface PositionGpsServiceInterface {

    PositionGpsDto enregistrerPosition(
            PositionGpsDto positionGpsDto,
            String username
    );

    PositionGpsDto getDernierePosition(Long suiviTrajetId);

    List<PositionGpsDto> getHistoriquePositions(
            Long suiviTrajetId
    );
}