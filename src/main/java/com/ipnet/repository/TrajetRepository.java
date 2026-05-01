package com.ipnet.repository;

import com.ipnet.entity.TrajetEntity;
import com.ipnet.entity.VilleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "trajetEntities", path = "trajetEntities")
public interface TrajetRepository extends JpaRepository<TrajetEntity, UUID> {
    
    
    List<TrajetEntity> findByVilleDepart(VilleEntity ville);
    

    List<TrajetEntity> findByVilleDepart_NomVille(String nom);
}