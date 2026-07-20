package com.ipnet.repository;

import com.ipnet.entity.TrajetEntity;
import com.ipnet.entity.VilleEntity;
import com.ipnet.enums.StatutTrajet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TrajetRepository extends JpaRepository<TrajetEntity, UUID> {

    List<TrajetEntity> findByVilleDepart(VilleEntity ville);

    List<TrajetEntity> findByVilleDepart_NomVille(String nom);

    List<TrajetEntity> findByStatut(StatutTrajet statut);

    List<TrajetEntity> findByChauffeur_Id(Long chauffeurId);

    List<TrajetEntity> findByAgence_Id(UUID agenceId);

    List<TrajetEntity> findByVilleDepart_IdAndVilleArrivee_Id(UUID departId, UUID arriveeId);

    List<TrajetEntity> findByVehicule_IdAndDateDepart(UUID vehiculeId, LocalDate date);

    List<TrajetEntity> findByChauffeur_IdAndDateDepart(Long chauffeurId, LocalDate date);
}