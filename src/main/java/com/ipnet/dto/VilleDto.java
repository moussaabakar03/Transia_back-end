package com.ipnet.dto;

import java.util.UUID;

public class VilleDto {

    private UUID id;
    private String nomVille;
    private String region;
    private String pays;

    public VilleDto() {
    }

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNomVille() { return nomVille; }
    public void setNomVille(String nomVille) { this.nomVille = nomVille; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
}
