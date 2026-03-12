package com.ipnet.entity;

import com.ipnet.enums.StatutPaiement;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import com.ipnet.entity.Reservation;

@Entity
public class PaiementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double montant;
    private String methode; // Mobile Money, Espèces
    private StatutPaiement statut;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}