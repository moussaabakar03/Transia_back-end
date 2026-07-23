package com.ipnet.security.dto;


import java.util.Set;
import java.util.UUID;

import com.ipnet.security.enums.StatutCompte;
import com.ipnet.security.enums.StatutOperationnel;
import com.ipnet.security.enums.UserRole;


public class UserDTO {

    private Long id;
    private String fullName;
    private String telephone;
    private String email;
    private String password;
    private Set<UserRole> roles;

    private StatutCompte statutCompte;
    private UUID publicId;
    private UUID agenceId;
    private UUID villeBaseId;
    private UUID villeActuelleId;
    private StatutOperationnel statutOperationnel;

    public UserDTO() {
    }

    public UserDTO(String fullName, String telephone, String password, Set<UserRole> roles, StatutCompte statutCompte, UUID publicId) {
        this.fullName = fullName;
        this.telephone = telephone;
        this.password = password;
        this.roles = roles;
        this.statutCompte = statutCompte;
        this.publicId = publicId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public StatutCompte getStatutCompte() {
        return statutCompte;
    }

    public void setStatutCompte(StatutCompte statutCompte) {
        this.statutCompte = statutCompte;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getAgenceId() { return agenceId; }
    public void setAgenceId(UUID agenceId) { this.agenceId = agenceId; }

    public UUID getVilleBaseId() { return villeBaseId; }
    public void setVilleBaseId(UUID villeBaseId) { this.villeBaseId = villeBaseId; }

    public UUID getVilleActuelleId() { return villeActuelleId; }
    public void setVilleActuelleId(UUID villeActuelleId) { this.villeActuelleId = villeActuelleId; }

    public StatutOperationnel getStatutOperationnel() { return statutOperationnel; }
    public void setStatutOperationnel(StatutOperationnel s) { this.statutOperationnel = s; }
}
