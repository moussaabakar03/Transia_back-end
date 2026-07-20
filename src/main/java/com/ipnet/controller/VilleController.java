package com.ipnet.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.dto.VilleDto;
import com.ipnet.services.interfaces.VilleServiceInterface;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/ville")
public class VilleController {

    private VilleServiceInterface villeService;

    public VilleController(VilleServiceInterface villeService) {
        this.villeService = villeService;
    }

    // Package Géographie : création/modification ouvertes à SUPER_ADMIN et ADMIN_AGENCE,
    // suppression réservée à SUPER_ADMIN (une ville peut être référencée par des agences/trajets/véhicules
    // d'autres agences que celle qui l'a créée).
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    public VilleDto create(@RequestBody VilleDto villeDto) {
        return villeService.create(villeDto);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<VilleDto> list() {
        return villeService.listeVille();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public VilleDto get(@PathVariable UUID id) {
        return villeService.getVille(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    public VilleDto update(@RequestBody VilleDto villeDto, @PathVariable UUID id) {
        return villeService.update(villeDto, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void delete(@PathVariable UUID id) {
        villeService.delete(id);
    }
}
