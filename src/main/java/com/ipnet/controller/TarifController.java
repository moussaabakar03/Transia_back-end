package com.ipnet.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ipnet.dto.EstimationPrixDto;
import com.ipnet.dto.TarifExpeditionDto;
import com.ipnet.dto.TarifExpeditionRequestDto;
import com.ipnet.enums.ModeRemise;
import com.ipnet.enums.TranchePoids;
import com.ipnet.services.interfaces.TarifExpeditionServiceInterface;

@RestController
@RequestMapping("/api/v1/colis/tarifs")
@CrossOrigin("*")
public class TarifController {

    private final TarifExpeditionServiceInterface tarifService;

    public TarifController(TarifExpeditionServiceInterface tarifService) {
        this.tarifService = tarifService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<TarifExpeditionDto> creerTarif(@RequestBody TarifExpeditionRequestDto dto) {
        return new ResponseEntity<>(tarifService.creerTarif(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<TarifExpeditionDto> modifierTarif(
            @PathVariable UUID id, @RequestBody TarifExpeditionRequestDto dto) {
        return ResponseEntity.ok(tarifService.modifierTarif(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> supprimerTarif(@PathVariable UUID id) {
        tarifService.supprimerTarif(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TarifExpeditionDto>> listerTarifs() {
        return ResponseEntity.ok(tarifService.listerTarifs());
    }

    @GetMapping("/villes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TarifExpeditionDto>> listerTarifsParVilles(
            @RequestParam UUID departId, @RequestParam UUID arriveeId) {
        return ResponseEntity.ok(tarifService.listerTarifsParVilles(departId, arriveeId));
    }

    @GetMapping("/estimer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EstimationPrixDto> estimerPrix(
            @RequestParam UUID departId,
            @RequestParam UUID arriveeId,
            @RequestParam TranchePoids tranche,
            @RequestParam ModeRemise modeRemise,
            @RequestParam(defaultValue = "false") boolean collecteDomicile) {
        return ResponseEntity.ok(
                tarifService.estimerPrix(departId, arriveeId, tranche, modeRemise, collecteDomicile));
    }
}
