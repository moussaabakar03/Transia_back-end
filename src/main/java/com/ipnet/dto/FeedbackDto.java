package com.ipnet.dto;

import java.util.UUID;

public class FeedbackDto {
    private int noteValeur;
    private String commentaireTexte;
    private UUID trajetId;
    private String creerPar; 

    public FeedbackDto() {}

    public int getNoteValeur() { return noteValeur; }
    public void setNoteValeur(int noteValeur) { this.noteValeur = noteValeur; }
    public String getCommentaireTexte() { return commentaireTexte; }
    public void setCommentaireTexte(String commentaireTexte) { this.commentaireTexte = commentaireTexte; }
    public UUID getTrajetId() { return trajetId; }
    public void setTrajetId(UUID trajetId) { this.trajetId = trajetId; }
    public String getCreerPar() { return creerPar; }
    public void setCreerPar(String creerPar) { this.creerPar = creerPar; }
}