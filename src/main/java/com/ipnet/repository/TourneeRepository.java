package com.ipnet.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.Tournee;

@Repository
public interface TourneeRepository extends JpaRepository<Tournee, UUID> {

    // List<Tournee> findByLivreurId(UUID livreurId);
    @Query("SELECT t FROM Tournee t WHERE t.livreur.publicId = :publicId")
    List<Tournee> findByLivreurPublicId(@Param("publicId") UUID publicId);


    List<Tournee> findByDateTournee(LocalDate dateTournee);

    List<Tournee> findByZone(String zone);

    @Query("SELECT t FROM Tournee t WHERE " +
           "(:date IS NULL OR t.dateTournee = :date) AND " +
           "(:livreurId IS NULL OR t.livreur.id = :livreurId) AND " +
           "(:zone IS NULL OR LOWER(t.zone) LIKE LOWER(CONCAT('%', :zone, '%')))")
    List<Tournee> findByFilters(
            @Param("date") LocalDate date,
            @Param("livreurId") UUID livreurId,
            @Param("zone") String zone);

    List<Tournee> findByStatut(String statut);
}
