package com.ipnet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.entity.TransportEntity;

public interface TransportRepository extends JpaRepository<TransportEntity, Long> {
    List<TransportEntity> findByVilleDepartAndVilleDestination(String depart, String destination);
}