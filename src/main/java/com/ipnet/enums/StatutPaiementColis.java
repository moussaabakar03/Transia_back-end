package com.ipnet.enums;

// Distinct de StatutPaiement (résultat d'une tentative de transaction) : celui-ci décrit l'état
// global du règlement d'un colis, qui peut être soldé en plusieurs fois (frais de collecte puis solde).
public enum StatutPaiementColis {
    EN_ATTENTE,
    PARTIELLEMENT_PAYE,
    PAYE
}
