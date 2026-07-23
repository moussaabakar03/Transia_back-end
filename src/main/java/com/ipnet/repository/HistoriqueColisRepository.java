package com.ipnet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.HistoriqueColis;

@Repository
public interface HistoriqueColisRepository extends JpaRepository<HistoriqueColis, UUID> {

    List<HistoriqueColis> findByColisIdOrderByDateChangementDesc(UUID colisId);

    List<HistoriqueColis> findByColisId(UUID colisId);
}
