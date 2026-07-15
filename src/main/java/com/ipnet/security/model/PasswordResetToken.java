package com.ipnet.security.model;

import com.ipnet.utils.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken extends BaseEntity implements Serializable {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date_expiration", nullable = false)
    private Instant dateExpiration;

    @Column(name = "utilise", nullable = false)
    private boolean utilise = false;

    public PasswordResetToken() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Instant getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(Instant dateExpiration) { this.dateExpiration = dateExpiration; }

    public boolean isUtilise() { return utilise; }
    public void setUtilise(boolean utilise) { this.utilise = utilise; }
}
