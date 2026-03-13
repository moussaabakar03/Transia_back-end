package com.ipnet.entity;

import java.util.List;

import com.ipnet.utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="Ville")
public class VilleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="NomVille", nullable=false, length=150)
    private String nomVille;

    @Column(name="Region", nullable=true, length=150)
    private String region;

    @OneToMany(mappedBy = "ville", fetch = FetchType.LAZY)
    private List<TrajetEntity> trajets;

    public VilleEntity() {
    }

    public VilleEntity(Long id, String nomVille, String region) {
        super();
        this.id = id;
        this.nomVille = nomVille;
        this.region = region;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomVille() { return nomVille; }
    public void setNomVille(String nomVille) { this.nomVille = nomVille; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public List<TrajetEntity> getTrajets() { return trajets; }
    public void setTrajets(List<TrajetEntity> trajets) { this.trajets = trajets; }
}