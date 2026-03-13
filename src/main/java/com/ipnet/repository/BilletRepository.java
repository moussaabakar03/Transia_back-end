package com.ipnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.entity.BilletEntity;

public interface BilletRepository extends JpaRepository<BilletEntity, Long> {}