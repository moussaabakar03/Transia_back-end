package com.ipnet.security.service;

import java.util.UUID;

import com.ipnet.security.dto.ProfilDTO;

public interface ProfilService {
    ProfilDTO getProfilByUserId(Long userId);
    ProfilDTO updateProfil(Long userId, ProfilDTO dto);
    
    ProfilDTO getProfilByPublicId(UUID publicId);
    ProfilDTO updateProfilByPublicId(UUID publicId, ProfilDTO dto);
}
