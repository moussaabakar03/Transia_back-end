package com.ipnet.repository;

import com.ipnet.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {
    List<FeedbackEntity> findByTrajetId(Long trajetId);
}