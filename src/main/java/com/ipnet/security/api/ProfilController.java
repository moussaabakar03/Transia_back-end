package com.ipnet.security.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ipnet.security.SecurityUtils;
import com.ipnet.security.dto.ProfilDTO;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.security.service.ProfilService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/profil")
@PreAuthorize("isAuthenticated()")
public class ProfilController {

    private final ProfilService profilService;
    private final UserRepository userRepository;

    public ProfilController(ProfilService profilService, UserRepository userRepository) {
        this.profilService = profilService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ProfilDTO getMonProfil() {
        User user = SecurityUtils.getConnectedUser(userRepository);
        return profilService.getProfilByUserId(user.getId());
    }

    @PutMapping("/me")
    public ProfilDTO updateMonProfil(@RequestBody ProfilDTO dto) {
        User user = SecurityUtils.getConnectedUser(userRepository);
        return profilService.updateProfil(user.getId(), dto);
    }

    @PostMapping("/me")
    public ResponseEntity<ProfilDTO> creerMonProfil(@RequestBody ProfilDTO dto) {
        User user = SecurityUtils.getConnectedUser(userRepository);
        dto.setUserId(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(profilService.createProfil(dto));
    }
}
