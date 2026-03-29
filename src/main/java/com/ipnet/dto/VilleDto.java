package com.ipnet.dto;

public class VilleDto {

    private Long id;
    private String nomVille;
    private String region;

    public VilleDto() {
    }

    public VilleDto(Long id, String nomVille, String region) {
        super();
        this.id = id;
        this.nomVille = nomVille;
        this.region = region;
    }

    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomVille() { return nomVille; }
    public void setNomVille(String nomVille) { this.nomVille = nomVille; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}