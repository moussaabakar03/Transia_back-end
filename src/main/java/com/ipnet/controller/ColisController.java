package com.ipnet.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.dto.AffectationLivreurRequestDto;
import com.ipnet.dto.ColisDto;
import com.ipnet.dto.ColisRequestDto;
import com.ipnet.dto.ColisStatutDto;
import com.ipnet.dto.HistoriqueColisDto;
import com.ipnet.dto.PeseeRequestDto;
import com.ipnet.enums.StatutColis;
import com.ipnet.services.interfaces.ColisServiceInterface;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/colis")
@CrossOrigin("*")
public class ColisController {

    private final ColisServiceInterface colisService;

    public ColisController(ColisServiceInterface colisService) {
        this.colisService = colisService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT','AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<ColisDto> enregistrerColis(
            @RequestBody ColisRequestDto dto) {
        return new ResponseEntity<>(
                colisService.enregistrerColis(dto),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}/pesee")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<ColisDto> confirmerPeseeAjusterPrix(
            @PathVariable UUID id,
            @RequestBody PeseeRequestDto dto) {
        return ResponseEntity.ok(
                colisService.confirmerPeseeAjusterPrix(
                        id,
                        dto.getPoidsReel(),
                        dto.getTrancheReelle()
                )
        );
    }

    @PutMapping("/{id}/charger")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<ColisDto> chargerColisInTrajet(
            @PathVariable UUID id,
            @RequestParam UUID trajetId) {
        return ResponseEntity.ok(
                colisService.chargerColisInTrajet(id, trajetId)
        );
    }

    @PutMapping("/{id}/receptionner")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<ColisDto> receptionnerColis(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                colisService.receptionnerColis(id)
        );
    }

    /**
     * Affecte ou réaffecte un colis arrivé à son agence de destination.
     * Cette opération ne démarre pas encore la livraison.
     */
    @PutMapping("/{id}/affectation-livreur")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<ColisDto> affecterLivreur(
            @PathVariable UUID id,
            @Valid @RequestBody AffectationLivreurRequestDto dto) {
        return ResponseEntity.ok(
                colisService.affecterLivreur(id, dto.getLivreurId())
        );
    }

    /**
     * Le livreur connecté démarre une livraison qui lui a déjà été affectée.
     */
    @PutMapping("/{id}/demarrer-livraison")
    @PreAuthorize("hasRole('LIVREUR')")
    public ResponseEntity<ColisDto> demarrerLivraison(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                colisService.demarrerLivraison(id)
        );
    }

    @PutMapping("/{id}/confirmer-livraison")
    @PreAuthorize("hasRole('LIVREUR')")
    public ResponseEntity<ColisDto> confirmerLivraison(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                colisService.confirmerLivraison(id)
        );
    }

    @GetMapping("/suivi/{numeroSuivi}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ColisStatutDto> getStatutColis(
            @PathVariable String numeroSuivi) {
        return ResponseEntity.ok(
                colisService.getStatutColis(numeroSuivi)
        );
    }

    @GetMapping("/mes-colis")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<ColisDto>> listerMesColis() {
        return ResponseEntity.ok(
                colisService.listerMesColis()
        );
    }

    @GetMapping("/mes-livraisons")
    @PreAuthorize("hasRole('LIVREUR')")
    public ResponseEntity<List<ColisDto>> listerMesLivraisons() {
        return ResponseEntity.ok(
                colisService.listerMesLivraisons()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<List<ColisDto>> listerColisParAgence(
            @RequestParam(required = false) UUID agenceId) {
        return ResponseEntity.ok(
                colisService.listerColisParAgence(agenceId)
        );
    }

    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<List<ColisDto>> listerColisParStatut(
            @PathVariable StatutColis statut) {
        return ResponseEntity.ok(
                colisService.listerColisParStatut(statut)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ColisDto> getById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                colisService.getById(id)
        );
    }

    @GetMapping("/{id}/historique")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HistoriqueColisDto>> getHistorique(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                colisService.getHistorique(id)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT_ACCUEIL','ADMIN_AGENCE','SUPER_ADMIN')")
    public ResponseEntity<Void> annulerColis(
            @PathVariable UUID id) {
        colisService.annulerColis(id);
        return ResponseEntity.noContent().build();
    }
}
