package com.ipnet.entity;

import com.ipnet.enums.StatutSuiviTrajet;
import com.ipnet.utils.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "suivis_trajets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_suivi_trajet",
                        columnNames = "trajet_id"
                )
        }
)
public class SuiviTrajetEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutSuiviTrajet statut = StatutSuiviTrajet.PROGRAMME;

    @Column(name = "date_demarrage")
    private LocalDateTime dateDemarrage;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "derniere_mise_a_jour")
    private LocalDateTime derniereMiseAJour;

    @Column(length = 500)
    private String message;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "trajet_id",
            nullable = false,
            unique = true
    )
    private TrajetEntity trajet;

    @OneToMany(
            mappedBy = "suiviTrajet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("dateHeure ASC")
    private List<PositionGpsEntity> historiquePositions = new ArrayList<>();

    public SuiviTrajetEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatutSuiviTrajet getStatut() {
        return statut;
    }

    public void setStatut(StatutSuiviTrajet statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateDemarrage() {
        return dateDemarrage;
    }

    public void setDateDemarrage(LocalDateTime dateDemarrage) {
        this.dateDemarrage = dateDemarrage;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public LocalDateTime getDerniereMiseAJour() {
        return derniereMiseAJour;
    }

    public void setDerniereMiseAJour(LocalDateTime derniereMiseAJour) {
        this.derniereMiseAJour = derniereMiseAJour;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TrajetEntity getTrajet() {
        return trajet;
    }

    public void setTrajet(TrajetEntity trajet) {
        this.trajet = trajet;
    }

    public List<PositionGpsEntity> getHistoriquePositions() {
        return historiquePositions;
    }

    public void setHistoriquePositions(
            List<PositionGpsEntity> historiquePositions
    ) {
        this.historiquePositions = historiquePositions;
    }

    public void ajouterPosition(PositionGpsEntity position) {
        historiquePositions.add(position);
        position.setSuiviTrajet(this);
    }
}