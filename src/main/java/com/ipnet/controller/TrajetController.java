package com.ipnet.controller;

import com.ipnet.dto.TrajetRequestDto;
import com.ipnet.dto.TrajetResponseDto;
import com.ipnet.security.SecurityUtils;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.TrajetService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/trajet")
public class TrajetController {

    private final TrajetService trajetService;
    private final UserRepository userRepository;

    public TrajetController(TrajetService trajetService, UserRepository userRepository) {
        this.trajetService = trajetService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    public TrajetResponseDto create(@RequestBody TrajetRequestDto request) {
        enforceAgenceScopeOnWrite(request);
        return trajetService.creerTrajet(request);
    }

    // Consultation ouverte à tout utilisateur connecté et non filtrée par agence
    // (le client doit pouvoir parcourir tous les trajets disponibles pour réserver, quelle que soit l'agence)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<TrajetResponseDto> list() {
        return trajetService.listerTousLesTrajets();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public TrajetResponseDto get(@PathVariable UUID id) {
        return trajetService.obtenirTrajet(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    public TrajetResponseDto update(@PathVariable UUID id, @RequestBody TrajetRequestDto request) {
        SecurityUtils.checkAgenceAccess(userRepository, trajetService.obtenirTrajet(id).getAgenceId());
        enforceAgenceScopeOnWrite(request);
        return trajetService.modifierTrajet(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    public void delete(@PathVariable UUID id) {
        SecurityUtils.checkAgenceAccess(userRepository, trajetService.obtenirTrajet(id).getAgenceId());
        trajetService.supprimerTrajet(id);
    }

    private void enforceAgenceScopeOnWrite(TrajetRequestDto request) {
        if (!SecurityUtils.hasRole("SUPER_ADMIN")) {
            UUID agenceId = SecurityUtils.getConnectedUserAgenceId(userRepository);
            if (agenceId == null) {
                throw new AccessDeniedException("Aucune agence associée à ce compte administrateur");
            }
            request.setAgenceId(agenceId);
        }
    }
}
