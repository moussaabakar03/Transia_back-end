package com.ipnet.services.implement;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.dto.EstimationPrixDto;
import com.ipnet.dto.TarifExpeditionDto;
import com.ipnet.dto.TarifExpeditionRequestDto;
import com.ipnet.entity.TarifExpeditionEntity;
import com.ipnet.entity.VilleEntity;
import com.ipnet.enums.ModeRemise;
import com.ipnet.enums.TranchePoids;
import com.ipnet.mappers.TarifExpeditionMapper;
import com.ipnet.repository.TarifExpeditionRepository;
import com.ipnet.repository.VilleRepository;
import com.ipnet.security.exception.AlreadyExistException;
import com.ipnet.security.exception.ResourceNotFoundException;
import com.ipnet.services.interfaces.TarifExpeditionServiceInterface;

@Service
public class TarifExpeditionServiceImpl implements TarifExpeditionServiceInterface {

    // Frais fixes de collecte/livraison à domicile (FCFA), appliqués en plus du prix d'expédition.
    public static final double FRAIS_COLLECTE_DOMICILE = 500.0;
    public static final double FRAIS_LIVRAISON_DOMICILE = 500.0;

    private final TarifExpeditionRepository tarifRepository;
    private final VilleRepository villeRepository;
    private final TarifExpeditionMapper tarifMapper;

    public TarifExpeditionServiceImpl(
            TarifExpeditionRepository tarifRepository,
            VilleRepository villeRepository,
            TarifExpeditionMapper tarifMapper) {
        this.tarifRepository = tarifRepository;
        this.villeRepository = villeRepository;
        this.tarifMapper = tarifMapper;
    }

    @Override
    @Transactional
    public TarifExpeditionDto creerTarif(TarifExpeditionRequestDto dto) {
        VilleEntity depart = getVille(dto.getVilleDepartId());
        VilleEntity arrivee = getVille(dto.getVilleArriveeId());

        tarifRepository.findByVilleDepartIdAndVilleArriveeIdAndTranchePoids(
                depart.getId(), arrivee.getId(), dto.getTranchePoids())
                .ifPresent(existing -> {
                    throw new AlreadyExistException(
                            "Un tarif existe déjà pour cette combinaison ville/tranche de poids");
                });

        TarifExpeditionEntity entity = new TarifExpeditionEntity();
        entity.setVilleDepart(depart);
        entity.setVilleArrivee(arrivee);
        entity.setTranchePoids(dto.getTranchePoids());
        entity.setTarif(dto.getTarif());

        return tarifMapper.toDto(tarifRepository.save(entity));
    }

    @Override
    @Transactional
    public TarifExpeditionDto modifierTarif(UUID id, TarifExpeditionRequestDto dto) {
        TarifExpeditionEntity entity = getTarif(id);

        VilleEntity depart = dto.getVilleDepartId() != null ? getVille(dto.getVilleDepartId()) : entity.getVilleDepart();
        VilleEntity arrivee = dto.getVilleArriveeId() != null ? getVille(dto.getVilleArriveeId()) : entity.getVilleArrivee();
        TranchePoids tranche = dto.getTranchePoids() != null ? dto.getTranchePoids() : entity.getTranchePoids();

        tarifRepository.findByVilleDepartIdAndVilleArriveeIdAndTranchePoids(depart.getId(), arrivee.getId(), tranche)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new AlreadyExistException(
                            "Un tarif existe déjà pour cette combinaison ville/tranche de poids");
                });

        entity.setVilleDepart(depart);
        entity.setVilleArrivee(arrivee);
        entity.setTranchePoids(tranche);
        if (dto.getTarif() != null) {
            entity.setTarif(dto.getTarif());
        }

        return tarifMapper.toDto(tarifRepository.save(entity));
    }

    @Override
    @Transactional
    public void supprimerTarif(UUID id) {
        TarifExpeditionEntity entity = getTarif(id);
        tarifRepository.delete(entity);
    }

    @Override
    public EstimationPrixDto estimerPrix(
            UUID villeDepartId, UUID villeArriveeId, TranchePoids tranche,
            ModeRemise modeRemise, boolean collecteDomicile) {
        TarifExpeditionEntity tarif = tarifRepository
                .findByVilleDepartIdAndVilleArriveeIdAndTranchePoids(villeDepartId, villeArriveeId, tranche)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun tarif configuré pour ce trajet et cette tranche de poids"));

        double fraisCollecte = collecteDomicile ? FRAIS_COLLECTE_DOMICILE : 0.0;
        double fraisLivraison = modeRemise == ModeRemise.LIVRAISON_DOMICILE ? FRAIS_LIVRAISON_DOMICILE : 0.0;

        return new EstimationPrixDto(tarif.getTarif(), fraisCollecte, fraisLivraison);
    }

    @Override
    public List<TarifExpeditionDto> listerTarifs() {
        return tarifMapper.toDtoList(tarifRepository.findAll());
    }

    @Override
    public List<TarifExpeditionDto> listerTarifsParVilles(UUID departId, UUID arriveeId) {
        return tarifMapper.toDtoList(tarifRepository.findByVilleDepartIdAndVilleArriveeId(departId, arriveeId));
    }

    private VilleEntity getVille(UUID id) {
        return villeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable"));
    }

    private TarifExpeditionEntity getTarif(UUID id) {
        return tarifRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarif d'expédition introuvable"));
    }
}
