package com.ipnet.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.VehiculeEntity;
import com.ipnet.enums.StatutVehicule;

@Repository
public interface VehiculeRepository extends JpaRepository<VehiculeEntity, UUID> {

    List<VehiculeEntity> findByStatut(StatutVehicule statut);

    // Clé du filtrage multi-agences : disponibles dans une ville précise
    List<VehiculeEntity> findByStatutAndVilleActuelle_Id(StatutVehicule statut, UUID villeId);

    List<VehiculeEntity> findByAgence_Id(UUID agenceId);

    Optional<VehiculeEntity> findByImmatriculation(String immatriculation);
}
