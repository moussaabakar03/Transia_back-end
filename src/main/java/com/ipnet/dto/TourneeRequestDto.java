package com.ipnet.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TourneeRequestDto {
    private LocalDate dateTournee;
    private UUID livreurId;
    private String zone;
    private List<UUID> colisIds;

    public LocalDate getDateTournee() {
        return dateTournee;
    }

    public void setDateTournee(LocalDate dateTournee) {
        this.dateTournee = dateTournee;
    }

    public UUID getLivreurId() {
        return livreurId;
    }

    public void setLivreurId(UUID livreurId) {
        this.livreurId = livreurId;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public List<UUID> getColisIds() {
        return colisIds;
    }

    public void setColisIds(List<UUID> colisIds) {
        this.colisIds = colisIds;
    }
}
