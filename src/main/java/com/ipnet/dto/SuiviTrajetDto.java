package com.ipnet.dto;

import java.util.List;
import java.util.UUID;

public class SuiviTrajetDto {
    private Long id;
    private String statut;
    private UUID trajetId;
    private List<PositionGpsDto> historiquePositions;

    public SuiviTrajetDto() {}

    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getTrajetId() { return trajetId; }
    public void setTrajetId(UUID trajetId) { this.trajetId = trajetId; }
    public List<PositionGpsDto> getHistoriquePositions() { return historiquePositions; }
    public void setHistoriquePositions(List<PositionGpsDto> historiquePositions) { this.historiquePositions = historiquePositions; }
}