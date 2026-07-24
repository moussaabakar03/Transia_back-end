package com.ipnet.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.ipnet.enums.TranchePoids;
import com.ipnet.utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tarif_expedition", uniqueConstraints = @UniqueConstraint(
        name = "uk_tarif_expedition_ville_tranche",
        columnNames = {"ville_depart_id", "ville_arrivee_id", "tranche_poids"}))
public class TarifExpeditionEntity extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ville_depart_id", nullable = false)
    private VilleEntity villeDepart;

    @ManyToOne
    @JoinColumn(name = "ville_arrivee_id", nullable = false)
    private VilleEntity villeArrivee;

    @Enumerated(EnumType.STRING)
    @Column(name = "tranche_poids", nullable = false)
    private TranchePoids tranchePoids;

    @Column(nullable = false)
    private Double tarif;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public VilleEntity getVilleDepart() { return villeDepart; }
    public void setVilleDepart(VilleEntity villeDepart) { this.villeDepart = villeDepart; }

    public VilleEntity getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(VilleEntity villeArrivee) { this.villeArrivee = villeArrivee; }

    public TranchePoids getTranchePoids() { return tranchePoids; }
    public void setTranchePoids(TranchePoids tranchePoids) { this.tranchePoids = tranchePoids; }

    public Double getTarif() { return tarif; }
    public void setTarif(Double tarif) { this.tarif = tarif; }
}
