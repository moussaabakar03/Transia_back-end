package com.ipnet.dto;

public class FeedbackDto {
    private int noteValeur;
    private String commentaireTexte;
    private Long trajetId;
    private String creerPar; 

    public FeedbackDto() {}

    public int getNoteValeur() { return noteValeur; }
    public void setNoteValeur(int noteValeur) { this.noteValeur = noteValeur; }
    public String getCommentaireTexte() { return commentaireTexte; }
    public void setCommentaireTexte(String commentaireTexte) { this.commentaireTexte = commentaireTexte; }
    public Long getTrajetId() { return trajetId; }
    public void setTrajetId(Long trajetId) { this.trajetId = trajetId; }
    public String getCreerPar() { return creerPar; }
    public void setCreerPar(String creerPar) { this.creerPar = creerPar; }
}