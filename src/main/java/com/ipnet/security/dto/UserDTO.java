package com.ipnet.security.dto;


import java.util.UUID;

import com.ipnet.security.model.Role;


public class UserDTO {

    private String fullName;
    private String username;
    private String password;
    private Role roles;
    private boolean enable;
    private UUID publicId;

    public UserDTO() {
    }

    public UserDTO(String fullName, String username, String password, Role roles, boolean enable, UUID publicId) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.enable = enable;
        this.publicId = publicId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRoles() {
        return roles;
    }

    public void setRoles(Role roles) {
        this.roles = roles;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

  

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }
}
