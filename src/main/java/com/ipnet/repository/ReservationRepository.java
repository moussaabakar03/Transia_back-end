package com.ipnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {}