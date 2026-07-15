package com.ipnet.repository;

import com.ipnet.entity.AgenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgenceRepository extends JpaRepository<AgenceEntity, UUID> {
    List<AgenceEntity> findByVille_Id(UUID villeId);
}
