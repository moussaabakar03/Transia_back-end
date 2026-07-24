package com.ipnet.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TourneeRequestDto {
    private LocalDate dateTournee;
    private UUID livreurId;
    private String zone;
    private List<UUID> demandeIds;

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

    public List<UUID> getDemandeIds() {
        return demandeIds;
    }

    public void setDemandeIds(List<UUID> demandeIds) {
        this.demandeIds = demandeIds;
    }
}
