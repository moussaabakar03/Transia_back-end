package com.ipnet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.entity.BilletEntity;

public interface BilletRepository extends JpaRepository<BilletEntity, UUID> {}
