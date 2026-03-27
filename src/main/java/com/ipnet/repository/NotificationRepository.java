package com.ipnet.repository;

import com.ipnet.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    /**
     * Récupère toutes les notifications d'un utilisateur spécifique,
     * triées de la plus récente à la plus ancienne.
     */
    List<NotificationEntity> findByDestinataireIdOrderByDateCreationDesc(Long userId);

    /**
     * Récupère uniquement les notifications non lues d'un utilisateur.
     * Utile pour afficher un badge (ex: "3 nouvelles notifications").
     */
    List<NotificationEntity> findByDestinataireIdAndLuFalse(Long userId);

    /**
     * Compte le nombre de notifications non lues.
     */
    long countByDestinataireIdAndLuFalse(Long userId);
}