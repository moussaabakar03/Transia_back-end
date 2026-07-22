package com.ipnet.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.dto.PaiementRequestDto;
import com.ipnet.dto.ReservationResponseDto;
import com.ipnet.services.interfaces.PaiementServiceInterface;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/paiements")
public class PaiementController {

    @Autowired private PaiementServiceInterface paiementService;

    // Paiement encaissé en personne par le staff (espèces) : jamais accessible à un client.
    @PostMapping("/caisse")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','AGENT_ACCUEIL')")
    public ResponseEntity<String> payerAuGuichet(@RequestBody PaiementRequestDto dto) {
        paiementService.validerPaiementCaisse(dto);
        return ResponseEntity.ok("Paiement encaissé avec succès. Billets confirmés.");
    }

    // Paiement effectué par le client lui-même depuis l'app (mobile money simulé) — l'appartenance
    // de la réservation est vérifiée côté service via le JWT.
    @PostMapping("/en-ligne")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponseDto> payerEnLigne(@RequestBody PaiementRequestDto dto) {
        return ResponseEntity.ok(paiementService.payerEnLigne(dto));
    }

    @GetMapping("/caisse")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','AGENT_ACCUEIL')")
    public List<PaiementRequestDto> listePaiementCaisse() {
		return paiementService.listePaiementCaisse();
	}


    @PutMapping("caisse/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','AGENT_ACCUEIL')")
    public PaiementRequestDto update(@RequestBody PaiementRequestDto dto, @PathVariable UUID id) {
    	return paiementService.update(dto, id);
    }

    // Suppression = annuler un paiement déjà enregistré (remet la réservation en EN_ATTENTE) :
    // action sensible, réservée à SUPER_ADMIN.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void delete(@PathVariable UUID id) {
        paiementService.delete(id);
    }

    @GetMapping("/caisse/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','AGENT_ACCUEIL')")
    public PaiementRequestDto getPaiementCaisse(@PathVariable UUID id) {
    	return paiementService.getPaiementCaisse(id);
    }


}

