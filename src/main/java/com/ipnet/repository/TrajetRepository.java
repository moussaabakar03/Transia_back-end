package com.ipnet.repository;

import com.ipnet.entity.TrajetEntity;
import com.ipnet.entity.VilleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "trajetEntities", path = "trajetEntities")
public interface TrajetRepository extends JpaRepository<TrajetEntity, UUID> {
    
    // Recherche par l'entité Ville directement
    List<TrajetEntity> findByVilleDepart(VilleEntity ville);
    
    // Ou recherche par le nom à l'intérieur de l'entité Ville
    List<TrajetEntity> findByVilleDepart_NomVille(String nom);
}