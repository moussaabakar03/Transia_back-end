package com.ipnet.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * Données nécessaires pour affecter un colis à un livreur.
 */
public class AffectationLivreurRequestDto {

    @NotNull(message = "L'identifiant du livreur est obligatoire")
    private UUID livreurId;

    public AffectationLivreurRequestDto() {
    }

    public AffectationLivreurRequestDto(UUID livreurId) {
        this.livreurId = livreurId;
    }

    public UUID getLivreurId() {
        return livreurId;
    }

    public void setLivreurId(UUID livreurId) {
        this.livreurId = livreurId;
    }
}
