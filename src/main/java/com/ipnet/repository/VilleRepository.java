package com.ipnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.VilleEntity;

@Repository
public interface VilleRepository extends JpaRepository<VilleEntity, Long> {
}