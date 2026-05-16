package com.ipnet.security.api;

import org.springframework.web.bind.annotation.*;

import com.ipnet.security.dto.ProfilDTO;
import com.ipnet.security.service.ProfilService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/profil")
public class ProfilController {

    private final ProfilService profilService;

    public ProfilController(ProfilService profilService) {
        this.profilService = profilService;
    }

    @GetMapping("/{userId}")
    public ProfilDTO getProfil(@PathVariable Long userId) {
        return profilService.getProfilByUserId(userId);
    }

    @PutMapping("/{userId}")
    public ProfilDTO updateProfil(@PathVariable Long userId, @RequestBody ProfilDTO dto) {
        return profilService.updateProfil(userId, dto);
    }
}