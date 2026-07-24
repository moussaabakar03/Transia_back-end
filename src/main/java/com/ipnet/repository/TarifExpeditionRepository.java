package com.ipnet.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.TarifExpeditionEntity;
import com.ipnet.enums.TranchePoids;

@Repository
public interface TarifExpeditionRepository extends JpaRepository<TarifExpeditionEntity, UUID> {

    Optional<TarifExpeditionEntity> findByVilleDepartIdAndVilleArriveeIdAndTranchePoids(
            UUID villeDepartId, UUID villeArriveeId, TranchePoids tranchePoids);

    List<TarifExpeditionEntity> findByVilleDepartId(UUID villeDepartId);

    List<TarifExpeditionEntity> findByVilleDepartIdAndVilleArriveeId(
            UUID villeDepartId, UUID villeArriveeId);
}
