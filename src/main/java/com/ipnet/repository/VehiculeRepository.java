package com.ipnet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.entity.VehiculeEntity;

public interface VehiculeRepository extends JpaRepository<VehiculeEntity, UUID>{

}
