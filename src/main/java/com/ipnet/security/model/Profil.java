package com.ipnet.security.model;

import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import com.ipnet.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "profils")
public class Profil extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "photo_profil")
    private String photoProfil; // URL de l'image

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "nom_complet", length = 150)
    private String nomComplet;

    @Column(name = "adresse")
    private String adresse;

    // Constructeurs
    public Profil() {}

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String photoProfil) { this.photoProfil = photoProfil; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
}