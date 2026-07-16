package com.ipnet.security.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.security.model.Profil;

import java.util.Optional;
import java.util.UUID;

public interface ProfilRepository extends JpaRepository<Profil, UUID> {
    Optional<Profil> findByUserId(Long userId);
}