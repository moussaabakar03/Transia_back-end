package com.ipnet.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ipnet.dto.DemandeCollecteDto;
import com.ipnet.dto.DemandeCollecteRequestDto;
import com.ipnet.services.interfaces.DemandeCollecteServiceInterface;

@RestController
@RequestMapping("/api/v1/colis/collecte")
@CrossOrigin("*")
public class DemandeCollecteController {

    private final DemandeCollecteServiceInterface demandeService;

    public DemandeCollecteController(DemandeCollecteServiceInterface demandeService) {
        this.demandeService = demandeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<DemandeCollecteDto> creerDemande(@RequestBody DemandeCollecteRequestDto dto) {
        return new ResponseEntity<>(demandeService.creerDemande(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/assigner")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<DemandeCollecteDto> assignerLivreur(
            @PathVariable UUID id, @RequestParam UUID livreurId) {
        return ResponseEntity.ok(demandeService.assignerLivreur(id, livreurId));
    }

    @PutMapping("/{id}/collecter")
    @PreAuthorize("hasRole('LIVREUR')")
    public ResponseEntity<DemandeCollecteDto> collecterColis(
            @PathVariable UUID id, @RequestParam(required = false) UUID colisId) {
        return ResponseEntity.ok(demandeService.collecterColis(id, colisId));
    }

    @PutMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('CLIENT','AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<DemandeCollecteDto> annulerDemande(@PathVariable UUID id) {
        return ResponseEntity.ok(demandeService.annulerDemande(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<List<DemandeCollecteDto>> listerDemandes(
            @RequestParam(required = false) UUID agenceId) {
        return ResponseEntity.ok(demandeService.listerDemandes(agenceId));
    }

    @GetMapping("/livreur/{id}")
    @PreAuthorize("hasRole('LIVREUR')")
    public ResponseEntity<List<DemandeCollecteDto>> listerDemandesLivreur(@PathVariable UUID id) {
        return ResponseEntity.ok(demandeService.listerDemandesLivreur(id));
    }

    // Absent de la spec initiale, ajouté pour que le client suive ses propres demandes.
    @GetMapping("/mes-demandes")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<DemandeCollecteDto>> listerMesDemandes() {
        return ResponseEntity.ok(demandeService.listerMesDemandes());
    }
}
