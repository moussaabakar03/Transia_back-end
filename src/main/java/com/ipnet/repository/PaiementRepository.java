package com.ipnet.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.PaiementEntity;

import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<PaiementEntity, Long> {
    
    Optional<PaiementEntity> findByReservationId(Long reservationId);
    
    Optional<PaiementEntity> findByReference(String reference);
}