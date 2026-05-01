package com.ipnet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.entity.VehiculeEntity;
import com.ipnet.enums.StatutVehicule;

public interface VehiculeRepository extends JpaRepository<VehiculeEntity, UUID>{
    List<VehiculeEntity> findByStatut(StatutVehicule statut);
}
