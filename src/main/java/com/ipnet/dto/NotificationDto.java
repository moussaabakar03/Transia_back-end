package com.ipnet.dto;

import java.time.LocalDateTime;

import com.ipnet.enums.TypeNotification;

public class NotificationDto {
    private Long id;
    private String titre;
    private String message;
    private boolean lu;
    private Long userId;
    private TypeNotification type;
    private String referenceMetier;
    private LocalDateTime dateEnvoi;

    public NotificationDto() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public TypeNotification getType() { return type; }
    public void setType(TypeNotification type) { this.type = type; }

    public String getReferenceMetier() { return referenceMetier; }
    public void setReferenceMetier(String referenceMetier) { this.referenceMetier = referenceMetier; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
}
