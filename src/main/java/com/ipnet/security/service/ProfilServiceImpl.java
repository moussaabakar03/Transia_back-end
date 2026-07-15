package com.ipnet.security.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.security.dto.ProfilDTO;
import com.ipnet.security.mappers.ProfilMapper;
import com.ipnet.security.model.Profil;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.ProfilRepository;
import com.ipnet.security.repository.UserRepository;

@Service
public class ProfilServiceImpl implements ProfilService {

    private final ProfilRepository profilRepository;
    private final ProfilMapper profilMapper;
    private final UserRepository userRepository;

    public ProfilServiceImpl(ProfilRepository profilRepository, ProfilMapper profilMapper, UserRepository userRepository) {
        this.profilRepository = profilRepository;
        this.profilMapper = profilMapper;
        this.userRepository = userRepository;
    }

    @Override
    public ProfilDTO getProfilByUserId(Long userId) {
        Profil profil = profilRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profil introuvable pour l'utilisateur " + userId));
        return profilMapper.toDto(profil);
    }

    @Override
    @Transactional
    public ProfilDTO updateProfil(Long userId, ProfilDTO dto) {
        Profil profil = profilRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profil introuvable pour l'utilisateur " + userId));
        profilMapper.updateEntity(profil, dto);
        Profil saved = profilRepository.save(profil);
        return profilMapper.toDto(saved);
    }

    @Override
    public ProfilDTO getProfilByPublicId(UUID publicId) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Profil profil = profilRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profil introuvable"));
        return profilMapper.toDto(profil);
    }

    @Override
    @Transactional
    public ProfilDTO updateProfilByPublicId(UUID publicId, ProfilDTO dto) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Profil profil = profilRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profil introuvable"));
        profilMapper.updateEntity(profil, dto);
        Profil saved = profilRepository.save(profil);
        return profilMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProfilDTO createProfil(ProfilDTO dto) {
        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID " + dto.getUserId()));

        // Vérifier que l'utilisateur n'a pas déjà un profil
        if (profilRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new RuntimeException("Un profil existe déjà pour cet utilisateur");
        }

        Profil profil = new Profil();
        profil.setUser(user);
        profil.setPhotoProfil(dto.getPhotoProfil());
        profil.setAdresse(dto.getAdresse());

        Profil saved = profilRepository.save(profil);
        return profilMapper.toDto(saved);
    }

    @Override
    public Optional<ProfilDTO> findByUserId(Long userId) {
        // Implémentation correcte
        return profilRepository.findByUserId(userId)
                .map(profilMapper::toDto);
    }
}