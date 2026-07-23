package com.ipnet.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ipnet.dto.ColisDto;
import com.ipnet.dto.ColisRequestDto;
import com.ipnet.dto.HistoriqueColisDto;
import com.ipnet.enums.StatutColis;
import com.ipnet.security.dto.UserDTO;
import com.ipnet.services.interfaces.ColisServiceInterface;

@RestController
@RequestMapping("/api/v1/colis")
@CrossOrigin("*")
public class ColisController {

    private final ColisServiceInterface colisService;

    public ColisController(ColisServiceInterface colisService) {
        this.colisService = colisService;
    }

    @PostMapping
    public ResponseEntity<ColisDto> create(@RequestBody ColisRequestDto dto) {
        return new ResponseEntity<>(colisService.create(dto), HttpStatus.CREATED);
    }

    @PostMapping("/demande-enlevement")
    public ResponseEntity<ColisDto> createDemandeEnlevement(@RequestBody ColisRequestDto dto) {
        return new ResponseEntity<>(colisService.createDemandeEnlevement(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ColisDto>> listColis(
            @RequestParam(required = false) StatutColis statut,
            @RequestParam(required = false) UUID livreurId,
            @RequestParam(required = false) UUID expediteurId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(colisService.filterColis(statut, livreurId, expediteurId, search));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<ColisDto> getById(@PathVariable UUID publicId) {
        return ResponseEntity.ok(colisService.getById(publicId));
    }

    @GetMapping("/suivi/{numeroSuivi}")
    public ResponseEntity<ColisDto> getByNumeroSuivi(@PathVariable String numeroSuivi) {
        return ResponseEntity.ok(colisService.getByNumeroSuivi(numeroSuivi));
    }

    @PatchMapping("/{publicId}")
    public ResponseEntity<ColisDto> updatePartial(
            @PathVariable UUID publicId,
            @RequestBody ColisRequestDto dto) {
        return ResponseEntity.ok(colisService.updatePartial(publicId, dto));
    }

    @PostMapping("/{publicId}/assigner-livreur")
    public ResponseEntity<ColisDto> assignerLivreur(
            @PathVariable UUID publicId,
            @RequestParam UUID livreurId) {
        return ResponseEntity.ok(colisService.assignerLivreur(publicId, livreurId));
    }

    @PostMapping("/{publicId}/collecter")
    public ResponseEntity<ColisDto> collecter(
            @PathVariable UUID publicId,
            @RequestParam(required = false) String commentaire) {
        return ResponseEntity.ok(colisService.collecter(publicId, commentaire));
    }

    @PostMapping("/{publicId}/livrer")
    public ResponseEntity<ColisDto> livrer(
            @PathVariable UUID publicId,
            @RequestParam(required = false) String commentaire) {
        return ResponseEntity.ok(colisService.livrer(publicId, commentaire));
    }

    @GetMapping("/{publicId}/historique")
    public ResponseEntity<List<HistoriqueColisDto>> getHistorique(@PathVariable UUID publicId) {
        return ResponseEntity.ok(colisService.getHistorique(publicId));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> annulerColis(@PathVariable UUID publicId) {
        colisService.annulerColis(publicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proximity/nearby")
    public ResponseEntity<List<ColisDto>> findNearby(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double distanceKm) {
        return ResponseEntity.ok(colisService.findNearby(latitude, longitude, distanceKm));
    }
}
