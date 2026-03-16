package com.ipnet.dto;

import java.time.LocalDateTime;

public class FeedbackResponseDto {
    private Long id;
    private int note;
    private String commentaire;
    private String username;
    private Long trajetId;
    private LocalDateTime dateCreation;

    public FeedbackResponseDto() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getTrajetId() { return trajetId; }
    public void setTrajetId(Long trajetId) { this.trajetId = trajetId; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}