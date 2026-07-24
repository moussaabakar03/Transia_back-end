package com.ipnet.controller;

import com.ipnet.dto.BilletDto;
import com.ipnet.services.interfaces.BilletServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billets")
@CrossOrigin("*")
public class BilletController {

    private final BilletServiceInterface billetService;

    public BilletController(BilletServiceInterface billetService) {
        this.billetService = billetService;
    }

    @PutMapping("/valider")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','CHAUFFEUR','AGENT_ACCUEIL')")
    public ResponseEntity<BilletDto> valider(@RequestParam String qrCode) {
        return ResponseEntity.ok(billetService.validerBillet(qrCode));
    }

    // Lecture seule (pas de changement de statut) : permet au chauffeur de savoir à quel trajet
    // appartient réellement un billet scanné qui ne correspond à aucun passager du trajet en cours.
    @GetMapping("/rechercher")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','CHAUFFEUR','AGENT_ACCUEIL')")
    public ResponseEntity<BilletDto> rechercher(@RequestParam String qrCode) {
        return ResponseEntity.ok(billetService.rechercherParQrCode(qrCode));
    }

    @GetMapping("/trajet/{trajetId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','CHAUFFEUR','AGENT_ACCUEIL')")
    public ResponseEntity<List<BilletDto>> getByTrajet(@PathVariable UUID trajetId) {
        return ResponseEntity.ok(billetService.getBilletsByTrajet(trajetId));
    }
}