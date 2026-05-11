package com.ipnet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ipnet.entity.BilletEntity;

public interface BilletRepository extends JpaRepository<BilletEntity, UUID> {
	
	@Query("SELECT b.numeroSiege FROM BilletEntity b WHERE b.reservation.trajet.id = :trajetId " +
	           "AND b.statut <> com.ipnet.enums.StatutBillet.ANNULE AND b.numeroSiege IS NOT NULL")
	List<String> findOccupiedSeatsByTrajetId(@Param("trajetId") UUID trajetId);
	
}
