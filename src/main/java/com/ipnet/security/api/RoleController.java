package com.ipnet.security.api;

import com.ipnet.security.dto.RoleDTO;
import com.ipnet.security.service.RoleService;
import com.ipnet.utils.BaseReponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@Tag(name = "Gestion des roles de l'utilisateur", description = "CRUD sur role")
@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    /* ================= CREATE ================= */
    @PostMapping("/save")
    public BaseReponse<RoleDTO> create(@RequestBody RoleDTO dto) {

        BaseReponse<RoleDTO> response = new BaseReponse<>();

        RoleDTO saved = service.saveRole(dto);

        response.setCodeRetour(201);
        response.setDescription("Rôle créé avec succès");
        response.setData(saved);

        return response;
    }

    /* ================= UPDATE ================= */
    @PutMapping("/update/{publicId}")
    public BaseReponse<RoleDTO> update(@PathVariable UUID publicId,
                                        @RequestBody RoleDTO dto) {

        BaseReponse<RoleDTO> response = new BaseReponse<>();

        RoleDTO updated = service.updateRole(publicId, dto);

        response.setCodeRetour(200);
        response.setDescription("Rôle modifié avec succès");
        response.setData(updated);

        return response;
    }

    /* ================= READ ONE ================= */
    @GetMapping("/get/{publicId}")
    public BaseReponse<RoleDTO> getByPublicId(@PathVariable UUID publicId) {

        BaseReponse<RoleDTO> response = new BaseReponse<>();

        RoleDTO role = service.getRoleByPublicId(publicId);

        response.setCodeRetour(200);
        response.setDescription("Rôle récupéré avec succès");
        response.setData(role);

        return response;
    }

    /* ================= READ ALL ================= */
    @GetMapping("/list")
    public BaseReponse<List<RoleDTO>> getAll() {

        BaseReponse<List<RoleDTO>> response = new BaseReponse<>();

        List<RoleDTO> roles = service.getAllRoles();

        response.setCodeRetour(200);
        response.setDescription("Liste des rôles récupérée avec succès");
        response.setData(roles);

        return response;
    }

    /* ================= DELETE ================= */
    @DeleteMapping("/delete/{publicId}")
    public BaseReponse<Void> delete(@PathVariable UUID publicId) {

        BaseReponse<Void> response = new BaseReponse<>();

        service.deleteRole(publicId);

        response.setCodeRetour(200);
        response.setDescription("Rôle supprimé avec succès");
        response.setData(null);

        return response;
    }
}
