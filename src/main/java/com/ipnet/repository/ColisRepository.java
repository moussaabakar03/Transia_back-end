package com.ipnet.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.Colis;
import com.ipnet.enums.ModeDepot;
import com.ipnet.enums.StatutColis;

@Repository
public interface ColisRepository extends JpaRepository<Colis, UUID> {
        
    List<Colis> findByStatut(StatutColis statut);

    @Query("SELECT c FROM Colis c WHERE c.livreur.publicId = :publicId")
    List<Colis> findByLivreurPublicId(@Param("publicId") UUID publicId);

    @Query("SELECT c FROM Colis c WHERE c.expediteur.publicId = :publicId")
    List<Colis> findByExpediteurPublicId(@Param("publicId") UUID publicId);

    // Utilise le champ spécifique de l'entité Colis (LocalDateTime)
    List<Colis> findByDateCreationColisBetween(LocalDateTime debut, LocalDateTime fin);

    List<Colis> findByNumeroSuivi(String numeroSuivi);

    @Query("SELECT c FROM Colis c WHERE " +
            "(:statut IS NULL OR c.statut = :statut) AND " +
            "(:livreurId IS NULL OR c.livreur.publicId = :livreurId) AND " +
            "(:expediteurId IS NULL OR c.expediteur.publicId = :expediteurId) AND " +
            "(:search IS NULL OR " +
            "LOWER(c.nomDestinataire) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.numeroSuivi) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.adresseDestinataire) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Colis> findByFilters(
            @Param("statut") StatutColis statut,
            @Param("livreurId") UUID livreurId,
            @Param("expediteurId") UUID expediteurId,
            @Param("search") String search);

    @Query("SELECT c FROM Colis c WHERE " +
            "c.latitudeDestinataire IS NOT NULL AND " +
            "c.longitudeDestinataire IS NOT NULL AND " +
            "(:lat IS NOT NULL AND :lon IS NOT NULL AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(c.latitudeDestinataire)) * " +
            "cos(radians(c.longitudeDestinataire) - radians(:lon)) + " +
            "sin(radians(:lat)) * sin(radians(c.latitudeDestinataire))) < :distance))")
    List<Colis> findNearby(
            @Param("lat") Double latitude,
            @Param("lon") Double longitude,
            @Param("distance") Double distanceKm);

    List<Colis> findByModeDepot(ModeDepot modeDepot);

    List<Colis> findByTourneeId(UUID tourneeId);

    @Query("SELECT COUNT(c) FROM Colis c WHERE c.statut = :statut")
    Long countByStatut(@Param("statut") StatutColis statut);
}