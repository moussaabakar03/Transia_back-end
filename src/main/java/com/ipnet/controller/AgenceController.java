package com.ipnet.controller;

import com.ipnet.dto.AgenceDto;
import com.ipnet.dto.AgenceStatutDto;
import com.ipnet.security.SecurityUtils;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.AgenceServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Gestion des agences")
@RestController
@RequestMapping("/api/v1/agences")
public class AgenceController {

    private final AgenceServiceInterface agenceService;
    private final UserRepository userRepository;

    public AgenceController(AgenceServiceInterface agenceService, UserRepository userRepository) {
        this.agenceService = agenceService;
        this.userRepository = userRepository;
    }

    // Créer une agence est réservé à SUPER_ADMIN : un ADMIN_AGENCE n'a par définition pas encore d'agence
    // à créer pour lui-même (elle doit exister avant qu'on puisse le rattacher dessus).
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Créer une agence")
    public ResponseEntity<AgenceDto> create(@RequestBody AgenceDto dto) {
        return new ResponseEntity<>(agenceService.create(dto), HttpStatus.CREATED);
    }

    // ADMIN_AGENCE peut modifier, mais uniquement sa propre agence (pas celle d'un concurrent).
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    @Operation(summary = "Modifier une agence")
    public ResponseEntity<AgenceDto> update(@PathVariable UUID id, @RequestBody AgenceDto dto) {
        SecurityUtils.checkAgenceAccess(userRepository, id);
        return ResponseEntity.ok(agenceService.update(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtenir une agence par ID")
    public ResponseEntity<AgenceDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(agenceService.getById(id));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Liste de toutes les agences")
    public ResponseEntity<List<AgenceDto>> getAll() {
        return ResponseEntity.ok(agenceService.getAll());
    }

    @GetMapping("/ville/{villeId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Agences d'une ville")
    public ResponseEntity<List<AgenceDto>> getByVille(@PathVariable UUID villeId) {
        return ResponseEntity.ok(agenceService.getByVille(villeId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Supprimer une agence")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        agenceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Activation/désactivation séparée de la modification générale : action volontaire et réservée
    // à SUPER_ADMIN (désactiver sa propre agence ne doit pas être à la portée d'un ADMIN_AGENCE).
    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Activer ou désactiver une agence")
    public ResponseEntity<AgenceDto> updateStatut(@PathVariable UUID id, @RequestBody AgenceStatutDto dto) {
        if (dto.getStatut() == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        return ResponseEntity.ok(agenceService.updateStatut(id, dto.getStatut()));
    }
}
