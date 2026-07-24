package com.ipnet.dto;

import com.ipnet.enums.TranchePoids;

public class PeseeRequestDto {
    private Double poidsReel;
    private TranchePoids trancheReelle;

    public Double getPoidsReel() { return poidsReel; }
    public void setPoidsReel(Double poidsReel) { this.poidsReel = poidsReel; }

    public TranchePoids getTrancheReelle() { return trancheReelle; }
    public void setTrancheReelle(TranchePoids trancheReelle) { this.trancheReelle = trancheReelle; }
}
