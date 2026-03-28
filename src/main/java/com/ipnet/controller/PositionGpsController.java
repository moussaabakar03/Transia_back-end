package com.ipnet.controller;

import com.ipnet.dto.PositionGpsDto;
import com.ipnet.services.interfaces.PositionGpsServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/positions-gps")
@CrossOrigin("*")
public class PositionGpsController {

    @Autowired private PositionGpsServiceInterface positionService;

    @PostMapping("/envoyer")
    public ResponseEntity<PositionGpsDto> envoyerPosition(@RequestBody PositionGpsDto dto) {
        return ResponseEntity.ok(positionService.enregistrerPosition(dto));
    }

    @GetMapping("/derniere/{suiviTrajetId}")
    public ResponseEntity<PositionGpsDto> voirBus(@PathVariable Long suiviTrajetId) {
        PositionGpsDto derniere = positionService.getDernierePosition(suiviTrajetId);
        return derniere != null ? ResponseEntity.ok(derniere) : ResponseEntity.noContent().build();
    }

    @GetMapping("/historique/{suiviTrajetId}")
    public ResponseEntity<List<PositionGpsDto>> historique(@PathVariable Long suiviTrajetId) {
        return ResponseEntity.ok(positionService.getHistoriquePositions(suiviTrajetId));
    }
}