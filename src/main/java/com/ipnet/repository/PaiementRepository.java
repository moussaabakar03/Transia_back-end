package com.ipnet.repository;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.PaiementEntity;

@Repository
public interface PaiementRepository extends JpaRepository<PaiementEntity, UUID> {
    
    Optional<PaiementEntity> findByReservationId(UUID reservationId);
    
    Optional<PaiementEntity> findByReference(String reference);
}
