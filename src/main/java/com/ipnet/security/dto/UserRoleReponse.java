package com.ipnet.security.dto;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import com.ipnet.security.enums.StatutCompte;
import com.ipnet.security.enums.StatutOperationnel;

public class UserRoleReponse {
    private Long id;
    private UUID publicId;
    private String fullName;
    private String telephone;
    private String email;
    private Date createdAt;
    private StatutCompte statutCompte;
    private Set<RoleDTO> roles;
    private UUID agenceId;
    private String agenceNom;
    private String villeNom;
    private UUID villeBaseId;
    private String villeBaseNom;
    private UUID villeActuelleId;
    private String villeActuelleNom;
    private StatutOperationnel statutOperationnel;
    private String photoProfil;

    public UserRoleReponse() {}

    public UserRoleReponse(Long id, UUID publicId, String fullName, String telephone, Date createdAt, StatutCompte statutCompte, Set<RoleDTO> roles) {
        this.id = id;
        this.publicId = publicId;
        this.fullName = fullName;
        this.telephone = telephone;
        this.createdAt = createdAt;
        this.statutCompte = statutCompte;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UUID getPublicId() { return publicId; }
    public void setPublicId(UUID publicId) { this.publicId = publicId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public StatutCompte getStatutCompte() { return statutCompte; }
    public void setStatutCompte(StatutCompte statutCompte) { this.statutCompte = statutCompte; }
    public Set<RoleDTO> getRoles() { return roles; }
    public void setRoles(Set<RoleDTO> roles) { this.roles = roles; }

    public UUID getAgenceId() { return agenceId; }
    public void setAgenceId(UUID agenceId) { this.agenceId = agenceId; }

    public String getAgenceNom() { return agenceNom; }
    public void setAgenceNom(String agenceNom) { this.agenceNom = agenceNom; }

    public String getVilleNom() { return villeNom; }
    public void setVilleNom(String villeNom) { this.villeNom = villeNom; }

    public UUID getVilleBaseId() { return villeBaseId; }
    public void setVilleBaseId(UUID villeBaseId) { this.villeBaseId = villeBaseId; }

    public String getVilleBaseNom() { return villeBaseNom; }
    public void setVilleBaseNom(String villeBaseNom) { this.villeBaseNom = villeBaseNom; }

    public UUID getVilleActuelleId() { return villeActuelleId; }
    public void setVilleActuelleId(UUID villeActuelleId) { this.villeActuelleId = villeActuelleId; }

    public String getVilleActuelleNom() { return villeActuelleNom; }
    public void setVilleActuelleNom(String villeActuelleNom) { this.villeActuelleNom = villeActuelleNom; }

    public StatutOperationnel getStatutOperationnel() { return statutOperationnel; }
    public void setStatutOperationnel(StatutOperationnel s) { this.statutOperationnel = s; }

    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String photoProfil) { this.photoProfil = photoProfil; }
}
