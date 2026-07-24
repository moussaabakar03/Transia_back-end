package com.ipnet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.NotificationEntity;
import com.ipnet.enums.TypeNotification;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByDestinataireIdOrderByDateEnvoiDesc(Long userId);

    List<NotificationEntity> findByDestinataireIdAndLuFalseOrderByDateEnvoiDesc(Long userId);

    long countByDestinataireIdAndLuFalse(Long userId);

    boolean existsByDestinataireIdAndTypeAndReferenceMetier(
        Long userId,
        TypeNotification type,
        String referenceMetier
    );
}
