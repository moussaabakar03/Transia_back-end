package com.ipnet.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ipnet.dto.VehiculeDto;
import com.ipnet.security.SecurityUtils;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.VehiculeServiceInterface;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/vehicule")
public class VehiculeController {

    private final VehiculeServiceInterface vehiculeService;
    private final UserRepository userRepository;

    public VehiculeController(VehiculeServiceInterface vehiculeService, UserRepository userRepository) {
        this.vehiculeService = vehiculeService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    public VehiculeDto create(@RequestBody VehiculeDto vehiculeDto) {
        enforceAgenceScopeOnWrite(vehiculeDto);
        return vehiculeService.create(vehiculeDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    public VehiculeDto update(@RequestBody VehiculeDto vehiculeDto, @PathVariable UUID id) {
        SecurityUtils.checkAgenceAccess(userRepository, vehiculeService.getVehicule(id).getAgenceId());
        enforceAgenceScopeOnWrite(vehiculeDto);
        return vehiculeService.update(vehiculeDto, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    public void delete(@PathVariable UUID id) {
        SecurityUtils.checkAgenceAccess(userRepository, vehiculeService.getVehicule(id).getAgenceId());
        vehiculeService.delete(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','AGENT_ACCUEIL')")
    public VehiculeDto getVehicule(@PathVariable UUID id) {
        VehiculeDto vehicule = vehiculeService.getVehicule(id);
        SecurityUtils.checkAgenceAccess(userRepository, vehicule.getAgenceId());
        return vehicule;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','AGENT_ACCUEIL')")
    public List<VehiculeDto> listeVehicule() {
        List<VehiculeDto> tous = vehiculeService.listeVehicule();
        if (SecurityUtils.hasRole("SUPER_ADMIN")) {
            return tous;
        }
        UUID agenceId = SecurityUtils.getConnectedUserAgenceId(userRepository);
        return tous.stream()
                .filter(v -> agenceId != null && agenceId.equals(v.getAgenceId()))
                .toList();
    }

    // Volontairement non filtré par agence : sert à trouver un véhicule disponible dans une ville,
    // y compris d'une autre agence, pour la coordination inter-agences (dispatch de trajets).
    @GetMapping("/disponible")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','AGENT_ACCUEIL')")
    public List<VehiculeDto> listeDisponibles(@RequestParam(required = false) UUID villeId) {
        if (villeId != null) {
            return vehiculeService.getDisponiblesByVille(villeId);
        }
        return vehiculeService.ListevehiculeDisponible();
    }

    private void enforceAgenceScopeOnWrite(VehiculeDto dto) {
        if (!SecurityUtils.hasRole("SUPER_ADMIN")) {
            UUID agenceId = SecurityUtils.getConnectedUserAgenceId(userRepository);
            if (agenceId == null) {
                throw new AccessDeniedException("Aucune agence associée à ce compte administrateur");
            }
            dto.setAgenceId(agenceId);
        }
    }
}
