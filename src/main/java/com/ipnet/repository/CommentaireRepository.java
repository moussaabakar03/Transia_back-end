package com.ipnet.repository;

import com.ipnet.entity.CommentaireEntity;
import com.ipnet.entity.TrajetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentaireRepository extends JpaRepository<CommentaireEntity, UUID> {

    Optional<CommentaireEntity> findTopByTrajetAndCreerParOrderByDateCreationDesc(
            TrajetEntity trajet,
            String creerPar
    );
}