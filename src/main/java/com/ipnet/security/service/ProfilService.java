package com.ipnet.security.service;

import java.util.Optional;
import java.util.UUID;

import com.ipnet.security.dto.ProfilDTO;

public interface ProfilService {
    ProfilDTO getProfilByUserId(Long userId);
    ProfilDTO updateProfil(Long userId, ProfilDTO dto);
    Optional<ProfilDTO> findByUserId(Long userId);
    ProfilDTO getProfilByPublicId(UUID publicId);
    ProfilDTO updateProfilByPublicId(UUID publicId, ProfilDTO dto);
    ProfilDTO createProfil(ProfilDTO dto);
}