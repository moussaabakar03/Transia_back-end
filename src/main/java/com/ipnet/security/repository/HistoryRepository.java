package com.ipnet.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ipnet.security.model.History;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findAllByOrderByDateHistoryDesc();
    
 
    
    // Optionnel : filtrer par utilisateur
    List<History> findByUserIdOrderByDateHistoryDesc(Long userId);
}
