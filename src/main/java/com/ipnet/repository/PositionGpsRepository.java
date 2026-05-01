package com.ipnet.repository;

import com.ipnet.entity.PositionGpsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PositionGpsRepository extends JpaRepository<PositionGpsEntity, Long> {
    Optional<PositionGpsEntity> findFirstBySuiviTrajetIdOrderByDateHeureDesc(Long suiviTrajetId);
}