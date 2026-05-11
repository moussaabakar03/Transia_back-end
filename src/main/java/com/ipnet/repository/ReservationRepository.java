package com.ipnet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ipnet.entity.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("SELECT SUM(r.nombrePlace) FROM Reservation r " +
           "WHERE r.trajet.id = :trajetId " +
           "AND r.statut IN (com.ipnet.enums.StatutReservation.EN_ATTENTE, com.ipnet.enums.StatutReservation.CONFIRMEE)")
    Integer sumPlacesOccupéesByTrajetId(@Param("trajetId") UUID trajetId);
    
    List<Reservation> findByTrajetId(UUID trajetId);
}