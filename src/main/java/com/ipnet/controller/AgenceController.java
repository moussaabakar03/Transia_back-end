package com.ipnet.controller;

import com.ipnet.dto.AgenceDto;
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

    public AgenceController(AgenceServiceInterface agenceService) {
        this.agenceService = agenceService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Créer une agence")
    public ResponseEntity<AgenceDto> create(@RequestBody AgenceDto dto) {
        return new ResponseEntity<>(agenceService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Modifier une agence")
    public ResponseEntity<AgenceDto> update(@PathVariable UUID id, @RequestBody AgenceDto dto) {
        return ResponseEntity.ok(agenceService.update(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une agence par ID")
    public ResponseEntity<AgenceDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(agenceService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Liste de toutes les agences")
    public ResponseEntity<List<AgenceDto>> getAll() {
        return ResponseEntity.ok(agenceService.getAll());
    }

    @GetMapping("/ville/{villeId}")
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
}
