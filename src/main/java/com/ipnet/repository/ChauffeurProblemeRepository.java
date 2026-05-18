package com.ipnet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.ChauffeurProblemeEntity;

@Repository
public interface ChauffeurProblemeRepository extends JpaRepository<ChauffeurProblemeEntity, UUID> {

    List<ChauffeurProblemeEntity> findByTrajet_Id(UUID trajetId);

    List<ChauffeurProblemeEntity> findByChauffeur_Id(Long chauffeurId);
}