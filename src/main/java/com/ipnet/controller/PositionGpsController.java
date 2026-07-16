package com.ipnet.controller;

import com.ipnet.dto.PositionGpsDto;
import com.ipnet.services.interfaces.PositionGpsServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/positions-gps")
@CrossOrigin("*")
public class PositionGpsController {

    private final PositionGpsServiceInterface positionService;

    public PositionGpsController(
            PositionGpsServiceInterface positionService
    ) {
        this.positionService = positionService;
    }

    @PostMapping("/envoyer")
    public ResponseEntity<PositionGpsDto> envoyerPosition(
            @RequestBody PositionGpsDto dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                positionService.enregistrerPosition(
                        dto,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/derniere/{suiviTrajetId}")
    public ResponseEntity<PositionGpsDto> voirBus(
            @PathVariable Long suiviTrajetId
    ) {
        PositionGpsDto derniere =
                positionService.getDernierePosition(
                        suiviTrajetId
                );

        if (derniere == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(derniere);
    }

    @GetMapping("/historique/{suiviTrajetId}")
    public ResponseEntity<List<PositionGpsDto>> historique(
            @PathVariable Long suiviTrajetId
    ) {
        return ResponseEntity.ok(
                positionService.getHistoriquePositions(
                        suiviTrajetId
                )
        );
    }
}