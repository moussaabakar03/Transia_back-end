package com.ipnet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.DemandeCollecteEntity;
import com.ipnet.enums.StatutCollecte;

@Repository
public interface DemandeCollecteRepository extends JpaRepository<DemandeCollecteEntity, UUID> {

    List<DemandeCollecteEntity> findByAgenceId(UUID agenceId);

    List<DemandeCollecteEntity> findByStatut(StatutCollecte statut);

    // User.id est un Long interne ; publicId (UUID) est l'identifiant externe utilisé partout ailleurs.
    List<DemandeCollecteEntity> findByLivreur_PublicId(UUID livreurPublicId);

    List<DemandeCollecteEntity> findByExpediteur_PublicId(UUID expediteurPublicId);

    List<DemandeCollecteEntity> findByAgenceIdAndStatut(UUID agenceId, StatutCollecte statut);
}
