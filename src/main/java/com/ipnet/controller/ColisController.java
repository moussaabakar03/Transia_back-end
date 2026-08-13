package com.ipnet.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ipnet.dto.ColisDto;
import com.ipnet.dto.ColisRequestDto;
import com.ipnet.dto.ColisStatutDto;
import com.ipnet.dto.HistoriqueColisDto;
import com.ipnet.dto.PeseeRequestDto;
import com.ipnet.enums.StatutColis;
import com.ipnet.services.interfaces.ColisServiceInterface;

@RestController
@RequestMapping("/api/v1/colis")
@CrossOrigin("*")
public class ColisController {

    private final ColisServiceInterface colisService;

    public ColisController(ColisServiceInterface colisService) {
        this.colisService = colisService;
    }

    // CLIENT ajouté : le formulaire mobile client appelle cet endpoint directement pour
    // l'auto-enregistrement (absent de la spec initiale qui ne prévoyait que AGENT/ADMIN,
    // mais nécessaire — c'est le seul moyen pour un client de créer un colis depuis l'app).
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT','AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<ColisDto> enregistrerColis(@RequestBody ColisRequestDto dto) {
        return new ResponseEntity<>(colisService.enregistrerColis(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/pesee")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<ColisDto> confirmerPeseeAjusterPrix(
            @PathVariable UUID id, @RequestBody PeseeRequestDto dto) {
        return ResponseEntity.ok(
                colisService.confirmerPeseeAjusterPrix(id, dto.getPoidsReel(), dto.getTrancheReelle()));
    }

    @PutMapping("/{id}/charger")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN','CHAUFFEUR')")
    public ResponseEntity<ColisDto> chargerColisInTrajet(
            @PathVariable UUID id, @RequestParam UUID trajetId) {
        return ResponseEntity.ok(colisService.chargerColisInTrajet(id, trajetId));
    }

    @PutMapping("/{id}/receptionner")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN','CHAUFFEUR')")
    public ResponseEntity<ColisDto> receptionnerColis(@PathVariable UUID id) {
        return ResponseEntity.ok(colisService.receptionnerColis(id));
    }

    @PutMapping("/{id}/demarrer-livraison")
    @PreAuthorize("hasAnyRole('LIVREUR','AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<ColisDto> demarrerLivraison(
            @PathVariable UUID id, @RequestParam UUID livreurId) {
        return ResponseEntity.ok(colisService.demarrerLivraison(id, livreurId));
    }

    @PutMapping("/{id}/confirmer-livraison")
    @PreAuthorize("hasAnyRole('LIVREUR','AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<ColisDto> confirmerLivraison(
            @PathVariable UUID id,
            @RequestParam(required = false) String codeOtp) {
        return ResponseEntity.ok(colisService.confirmerLivraison(id, codeOtp));
    }

    @GetMapping("/suivi/{numeroSuivi}")
    public ResponseEntity<ColisStatutDto> getStatutColis(@PathVariable String numeroSuivi) {
        return ResponseEntity.ok(colisService.getStatutColis(numeroSuivi));
    }

    // Absent de la spec (endpoints listés = agent/admin uniquement), ajouté pour que le client
    // mobile puisse lister les colis qu'il a lui-même envoyés.
    @GetMapping("/mes-colis")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<ColisDto>> listerMesColis() {
        return ResponseEntity.ok(colisService.listerMesColis());
    }

    @GetMapping("/mes-livraisons")
    @PreAuthorize("hasRole('LIVREUR')")
    public ResponseEntity<List<ColisDto>> listerMesLivraisons() {
        return ResponseEntity.ok(colisService.listerMesLivraisons());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<List<ColisDto>> listerColisParAgence(
            @RequestParam(required = false) UUID agenceId) {
        return ResponseEntity.ok(colisService.listerColisParAgence(agenceId));
    }

    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<List<ColisDto>> listerColisParStatut(@PathVariable StatutColis statut) {
        return ResponseEntity.ok(colisService.listerColisParStatut(statut));
    }

    @GetMapping("/trajet/{trajetId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ColisDto>> listerColisParTrajet(@PathVariable UUID trajetId) {
        return ResponseEntity.ok(colisService.listerColisParTrajet(trajetId));
    }

    // Conservés de l'existant, pas dans la spec mais utiles

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ColisDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(colisService.getById(id));
    }

    @GetMapping("/{id}/historique")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HistoriqueColisDto>> getHistorique(@PathVariable UUID id) {
        return ResponseEntity.ok(colisService.getHistorique(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<Void> annulerColis(@PathVariable UUID id) {
        colisService.annulerColis(id);
        return ResponseEntity.noContent().build();
    }
}
