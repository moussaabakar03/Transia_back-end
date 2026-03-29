package com.ipnet.repository;

import com.ipnet.entity.SuiviTrajetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SuiviTrajetRepository extends JpaRepository<SuiviTrajetEntity, Long> {
    Optional<SuiviTrajetEntity> findByTrajetId(Long trajetId);
}