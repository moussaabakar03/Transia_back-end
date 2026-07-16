package com.ipnet.controller;

import com.ipnet.dto.StatutSuiviRequestDto;
import com.ipnet.dto.SuiviTrajetDto;
import com.ipnet.enums.StatutSuiviTrajet;
import com.ipnet.services.interfaces.SuiviTrajetServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suivis")
@CrossOrigin("*")
public class SuiviTrajetController {

    private final SuiviTrajetServiceInterface suiviService;

    public SuiviTrajetController(
            SuiviTrajetServiceInterface suiviService
    ) {
        this.suiviService = suiviService;
    }

    @PostMapping("/creer/{trajetId}")
    public ResponseEntity<SuiviTrajetDto> creer(
            @PathVariable UUID trajetId
    ) {
        return ResponseEntity.ok(
                suiviService.creerOuRecupererSuivi(trajetId)
        );
    }

    @PostMapping("/demarrer/{trajetId}")
    public ResponseEntity<SuiviTrajetDto> demarrer(
            @PathVariable UUID trajetId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                suiviService.demarrerSuivi(
                        trajetId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{suiviId}/pause")
    public ResponseEntity<SuiviTrajetDto> pause(
            @PathVariable Long suiviId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                suiviService.mettreEnPause(
                        suiviId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{suiviId}/reprendre")
    public ResponseEntity<SuiviTrajetDto> reprendre(
            @PathVariable Long suiviId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                suiviService.reprendreSuivi(
                        suiviId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{suiviId}/terminer")
    public ResponseEntity<SuiviTrajetDto> terminer(
            @PathVariable Long suiviId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                suiviService.terminerSuivi(
                        suiviId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{suiviId}/annuler")
    public ResponseEntity<SuiviTrajetDto> annuler(
            @PathVariable Long suiviId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                suiviService.annulerSuivi(
                        suiviId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{suiviId}/statut")
    public ResponseEntity<SuiviTrajetDto> modifierStatut(
            @PathVariable Long suiviId,
            @RequestBody StatutSuiviRequestDto request,
            Authentication authentication
    ) {
        if (request == null
                || request.getStatut() == null
                || request.getStatut().isBlank()) {
            throw new RuntimeException(
                    "Le nouveau statut est obligatoire."
            );
        }

        StatutSuiviTrajet statut;

        try {
            statut = StatutSuiviTrajet.valueOf(
                    request.getStatut()
                            .trim()
                            .toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "Statut de suivi invalide."
            );
        }

        return ResponseEntity.ok(
                suiviService.mettreAJourStatut(
                        suiviId,
                        statut,
                        request.getMessage(),
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{suiviId}")
    public ResponseEntity<SuiviTrajetDto> consulterParId(
            @PathVariable Long suiviId
    ) {
        return ResponseEntity.ok(
                suiviService.getSuiviParId(suiviId)
        );
    }

    @GetMapping("/trajet/{trajetId}")
    public ResponseEntity<SuiviTrajetDto> consulterParTrajet(
            @PathVariable UUID trajetId
    ) {
        return ResponseEntity.ok(
                suiviService.getSuiviParTrajet(trajetId)
        );
    }
}