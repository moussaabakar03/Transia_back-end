package com.ipnet.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.entity.Colis;
import com.ipnet.enums.StatutColis;
import com.ipnet.enums.StatutPaiementColis;

@Repository
public interface ColisRepository extends JpaRepository<Colis, UUID> {

    Optional<Colis> findByNumeroSuivi(String numeroSuivi);

    List<Colis> findByAgenceDepartId(UUID agenceId);

    List<Colis> findByAgenceArriveeId(UUID agenceId);

    List<Colis> findByStatut(StatutColis statut);

    List<Colis> findByTrajetId(UUID trajetId);

    List<Colis> findByStatutPaiement(StatutPaiementColis statutPaiement);

    List<Colis> findByAgenceDepartIdAndStatut(UUID agenceId, StatutColis statut);

    // expediteurNom/Telephone sont des champs texte libres (pas de FK User, guichet compris) :
    // "mes colis" côté client se retrouve donc par téléphone, comme le fait le JWT du connecté.
    List<Colis> findByExpediteurTelephoneOrderByDateCreationColisDesc(String expediteurTelephone);

    // "mes livraisons" côté livreur : livreur est bien un FK User ici.
    List<Colis> findByLivreur_PublicIdOrderByDateCreationColisDesc(UUID livreurPublicId);

    boolean existsByLivreur_PublicIdAndStatut(UUID livreurPublicId, StatutColis statut);
}
