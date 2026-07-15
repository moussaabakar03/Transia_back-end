package com.ipnet.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ipnet.enums.StatutColis;
import com.ipnet.security.dto.UserDTO;

public class HistoriqueColisDto {
    private UUID id;
    private UUID colisId;
    private StatutColis ancienStatut;
    private StatutColis nouveauStatut;
    private LocalDateTime dateChangement;
    private UUID utilisateurId;
    private UserDTO utilisateur;
    private String commentaire;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getColisId() {
        return colisId;
    }

    public void setColisId(UUID colisId) {
        this.colisId = colisId;
    }

    public StatutColis getAncienStatut() {
        return ancienStatut;
    }

    public void setAncienStatut(StatutColis ancienStatut) {
        this.ancienStatut = ancienStatut;
    }

    public StatutColis getNouveauStatut() {
        return nouveauStatut;
    }

    public void setNouveauStatut(StatutColis nouveauStatut) {
        this.nouveauStatut = nouveauStatut;
    }

    public LocalDateTime getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }

    public UUID getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(UUID utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public UserDTO getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UserDTO utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
