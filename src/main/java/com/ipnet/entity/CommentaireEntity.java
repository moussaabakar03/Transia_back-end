package com.ipnet.entity;

import com.ipnet.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "commentaires")
public class CommentaireEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", nullable = false)
    private TrajetEntity trajet;

    public CommentaireEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public TrajetEntity getTrajet() { return trajet; }
    public void setTrajet(TrajetEntity trajet) { this.trajet = trajet; }
}