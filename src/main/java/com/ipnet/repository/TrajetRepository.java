package com.ipnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ipnet.entity.TrajetEntity;

@Repository
public interface TrajetRepository extends JpaRepository<TrajetEntity, Long> {
   
}