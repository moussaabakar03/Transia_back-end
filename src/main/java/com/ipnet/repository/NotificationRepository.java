package com.ipnet.repository;

import com.ipnet.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    
    List<NotificationEntity> findByDestinataireIdOrderByDateCreationDesc(Long userId);


    List<NotificationEntity> findByDestinataireIdAndLuFalse(Long userId);


    long countByDestinataireIdAndLuFalse(Long userId);
}