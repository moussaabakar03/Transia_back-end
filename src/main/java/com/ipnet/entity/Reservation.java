package com.ipnet.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.ipnet.enums.StatutReservation;
import com.ipnet.security.model.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateReservation;
    private LocalDateTime expiration; // Date de resa + 36h
    private int nombrePlace;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "trajet_id")
    private TrajetEntity trajet;

    
    // Relation 1-1 avec le paiement
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private PaiementEntity paiement;


    // Une réservation peut générer plusieurs billets (selon nombrePlace)
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<BilletEntity> billets;
    
    

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public LocalDateTime getDateReservation() {
		return dateReservation;
	}


	public void setDateReservation(LocalDateTime dateReservation) {
		this.dateReservation = dateReservation;
	}


	public LocalDateTime getExpiration() {
		return expiration;
	}


	public void setExpiration(LocalDateTime expiration) {
		this.expiration = expiration;
	}


	public int getNombrePlace() {
		return nombrePlace;
	}


	public void setNombrePlace(int nombrePlace) {
		this.nombrePlace = nombrePlace;
	}


	public StatutReservation getStatut() {
		return statut;
	}


	public void setStatut(StatutReservation statut) {
		this.statut = statut;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public TrajetEntity getTrajet() {
		return trajet;
	}


	public void setTrajet(TrajetEntity trajet) {
		this.trajet = trajet;
	}


	public PaiementEntity getPaiement() {
		return paiement;
	}


	public void setPaiement(PaiementEntity paiement) {
		this.paiement = paiement;
	}


	public List<BilletEntity> getBillets() {
		return billets;
	}


	public void setBillets(List<BilletEntity> billets) {
		this.billets = billets;
	}

    
    
}