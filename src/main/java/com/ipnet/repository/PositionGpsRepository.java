package com.ipnet.repository;

import com.ipnet.entity.PositionGpsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionGpsRepository
        extends JpaRepository<PositionGpsEntity, Long> {

    Optional<PositionGpsEntity>
    findFirstBySuiviTrajet_IdOrderByDateHeureDesc(
            Long suiviTrajetId
    );

    List<PositionGpsEntity>
    findBySuiviTrajet_IdOrderByDateHeureAsc(
            Long suiviTrajetId
    );

    long countBySuiviTrajet_Id(Long suiviTrajetId);
}