package com.ipnet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ipnet.utils.BaseEntity;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name="Ville")
public class VilleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="NomVille", nullable=false, length=150)
    private String nomVille;

    @Column(name="Region", length=100)
    private String region;

    
    @OneToMany(mappedBy = "villeDepart", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TrajetEntity> trajetsDepart;


    @OneToMany(mappedBy = "villeArrivee", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TrajetEntity> trajetsArrivee;

    public VilleEntity() {}


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomVille() { return nomVille; }
    public void setNomVille(String nomVille) { this.nomVille = nomVille; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public List<TrajetEntity> getTrajetsDepart() { return trajetsDepart; }
    public void setTrajetsDepart(List<TrajetEntity> trajetsDepart) { this.trajetsDepart = trajetsDepart; }

    public List<TrajetEntity> getTrajetsArrivee() { return trajetsArrivee; }
    public void setTrajetsArrivee(List<TrajetEntity> trajetsArrivee) { this.trajetsArrivee = trajetsArrivee; }
}