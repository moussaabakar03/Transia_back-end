package com.ipnet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.Reservation;

@Repository
public interface ljqfd extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
}