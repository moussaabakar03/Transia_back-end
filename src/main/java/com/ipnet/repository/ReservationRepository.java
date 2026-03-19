package com.ipnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ipnet.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@Query("SELECT SUM(r.nombrePlace) FROM Reservation r WHERE r.trajet.id = :trajetId")
    Integer sumPlacesReserveesByTrajetId(@Param("trajetId") Long trajetId);
}