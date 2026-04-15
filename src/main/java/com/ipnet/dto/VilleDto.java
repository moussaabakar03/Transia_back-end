package com.ipnet.dto;

import java.util.UUID;

public class VilleDto {

    private UUID id;
    private String nomVille;
    private String region;

    public VilleDto() {
    }

    public VilleDto(UUID id, String nomVille, String region) {
        super();
        this.id = id;
        this.nomVille = nomVille;
        this.region = region;
    }

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNomVille() { return nomVille; }
    public void setNomVille(String nomVille) { this.nomVille = nomVille; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
