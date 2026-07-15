package com.ipnet.security.model;

import com.ipnet.entity.AgenceEntity;
import com.ipnet.entity.VilleEntity;
import com.ipnet.security.enums.StatutCompte;
import com.ipnet.security.enums.StatutOperationnel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import com.ipnet.utils.BaseEntity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID publicId;

    @Column(name = "nom",nullable = false)
    private String nom;

    @Column(name = "telephone", nullable = false, unique = true)
    private String telephone;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password",nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_compte", nullable = false)
    private StatutCompte statutCompte = StatutCompte.ACTIF;

    // Un utilisateur peut cumuler plusieurs rôles (ex : CHAUFFEUR + LIVREUR)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profil profil;

    // null pour SUPER_ADMIN (vue globale), renseigné pour ADMIN_AGENCE, AGENT_ACCUEIL, CHAUFFEUR, LIVREUR
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agence_id", nullable = true)
    private AgenceEntity agence;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ville_base_id", nullable = true)
    private VilleEntity villeBase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ville_actuelle_id", nullable = true)
    private VilleEntity villeActuelle;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_operationnel")
    private StatutOperationnel statutOperationnel;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public StatutCompte getStatutCompte() {
        return statutCompte;
    }

    public void setStatutCompte(StatutCompte statutCompte) {
        this.statutCompte = statutCompte;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Profil getProfil() { return profil; }
    public void setProfil(Profil profil) { this.profil = profil; }

    public AgenceEntity getAgence() { return agence; }
    public void setAgence(AgenceEntity agence) { this.agence = agence; }

    public VilleEntity getVilleBase() { return villeBase; }
    public void setVilleBase(VilleEntity villeBase) { this.villeBase = villeBase; }

    public VilleEntity getVilleActuelle() { return villeActuelle; }
    public void setVilleActuelle(VilleEntity villeActuelle) { this.villeActuelle = villeActuelle; }

    public StatutOperationnel getStatutOperationnel() { return statutOperationnel; }
    public void setStatutOperationnel(StatutOperationnel statutOperationnel) { this.statutOperationnel = statutOperationnel; }
}
