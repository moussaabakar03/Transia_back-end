package com.ipnet.entity;

import com.ipnet.security.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int note;
    private String commentaire;
    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "trajet_id")
    private TrajetEntity trajet;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public FeedbackEntity() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public TrajetEntity getTrajet() { return trajet; }
    public void setTrajet(TrajetEntity trajet) { this.trajet = trajet; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}