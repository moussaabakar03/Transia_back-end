package com.ipnet.repository;

import com.ipnet.entity.TrajetEntity;
import com.ipnet.entity.VilleEntity;
import com.ipnet.enums.StatutTrajet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrajetRepository extends JpaRepository<TrajetEntity, UUID> {

    /**
     * Verrou pessimiste (SELECT ... FOR UPDATE) : utilisé lors de la création/modification d'une
     * réservation pour empêcher deux requêtes concurrentes de survendre les dernières places d'un trajet.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TrajetEntity t WHERE t.id = :id")
    Optional<TrajetEntity> findByIdForUpdate(UUID id);

    List<TrajetEntity> findByVilleDepart(VilleEntity ville);

    List<TrajetEntity> findByVilleDepart_NomVille(String nom);

    List<TrajetEntity> findByStatut(StatutTrajet statut);

    List<TrajetEntity> findByChauffeur_Id(Long chauffeurId);

    List<TrajetEntity> findByAgence_Id(UUID agenceId);

    List<TrajetEntity> findByVilleDepart_IdAndVilleArrivee_Id(UUID departId, UUID arriveeId);

    List<TrajetEntity> findByVehicule_IdAndDateDepart(UUID vehiculeId, LocalDate date);

    List<TrajetEntity> findByChauffeur_IdAndDateDepart(Long chauffeurId, LocalDate date);
}