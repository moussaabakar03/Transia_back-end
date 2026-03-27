package com.ipnet.dto;

public class NotificationDto {
    private Long id;
    private String titre;
    private String message;
    private boolean lu;
    private Long userId;

    public NotificationDto() {}

    // Getters et Setters
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
}