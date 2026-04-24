package com.ipnet.repository;
import com.ipnet.entity.CommentaireEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentaireRepository extends JpaRepository<CommentaireEntity, UUID> {}
