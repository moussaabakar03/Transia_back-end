package com.ipnet.services.implement;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.dto.TourneeDto;
import com.ipnet.dto.TourneeRequestDto;
import com.ipnet.entity.DemandeCollecteEntity;
import com.ipnet.entity.Tournee;
import com.ipnet.mappers.TourneeMapper;
import com.ipnet.repository.DemandeCollecteRepository;
import com.ipnet.repository.TourneeRepository;
import com.ipnet.security.exception.ResourceNotFoundException;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.TourneeServiceInterface;

@Service
public class TourneeServiceImpl implements TourneeServiceInterface {

    private final TourneeRepository tourneeRepository;
    private final DemandeCollecteRepository demandeRepository;
    private final UserRepository userRepository;
    private final TourneeMapper tourneeMapper;

    public TourneeServiceImpl(
            TourneeRepository tourneeRepository,
            DemandeCollecteRepository demandeRepository,
            UserRepository userRepository,
            TourneeMapper tourneeMapper) {
        this.tourneeRepository = tourneeRepository;
        this.demandeRepository = demandeRepository;
        this.userRepository = userRepository;
        this.tourneeMapper = tourneeMapper;
    }

    @Override
    @Transactional
    public TourneeDto create(TourneeRequestDto dto) {
        User livreur = userRepository.findByPublicId(dto.getLivreurId())
                .orElseThrow(() -> new ResourceNotFoundException("Livreur non trouvé"));

        Tournee tournee = tourneeMapper.toEntity(dto);
        tournee.setLivreur(livreur);
        tournee.setStatut("PLANIFIEE");

        Tournee savedTournee = tourneeRepository.save(tournee);

        if (dto.getDemandeIds() != null && !dto.getDemandeIds().isEmpty()) {
            for (UUID demandeId : dto.getDemandeIds()) {
                addDemandeToTournee(savedTournee.getId(), demandeId);
            }
        }

        return tourneeMapper.toDto(tourneeRepository.findById(savedTournee.getId()).orElse(savedTournee));
    }

    @Override
    public TourneeDto getById(UUID id) {
        Tournee tournee = tourneeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournée non trouvée avec l'ID : " + id));
        return tourneeMapper.toDto(tournee);
    }

    @Override
    public List<TourneeDto> listTournees() {
        return tourneeMapper.toDtoList(tourneeRepository.findAll());
    }

    @Override
    public List<TourneeDto> filterTournees(java.time.LocalDate date, UUID livreurId, String zone) {
        return tourneeMapper.toDtoList(tourneeRepository.findByFilters(date, livreurId, zone));
    }

    @Override
    @Transactional
    public TourneeDto updatePartial(UUID id, TourneeRequestDto dto) {
        Tournee tournee = tourneeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournée non trouvée avec l'ID : " + id));

        if (dto.getDateTournee() != null) {
            tournee.setDateTournee(dto.getDateTournee());
        }
        if (dto.getZone() != null) {
            tournee.setZone(dto.getZone());
        }
        if (dto.getLivreurId() != null) {
            User livreur = userRepository.findByPublicId(dto.getLivreurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Livreur non trouvé"));
            tournee.setLivreur(livreur);
        }

        return tourneeMapper.toDto(tourneeRepository.save(tournee));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Tournee tournee = tourneeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournée non trouvée avec l'ID : " + id));

        if (tournee.getDemandesCollecte() != null) {
            for (DemandeCollecteEntity demande : tournee.getDemandesCollecte()) {
                demande.setTournee(null);
                demandeRepository.save(demande);
            }
        }

        tourneeRepository.delete(tournee);
    }

    @Override
    @Transactional
    public TourneeDto addDemandeToTournee(UUID tourneeId, UUID demandeId) {
        Tournee tournee = tourneeRepository.findById(tourneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournée non trouvée avec l'ID : " + tourneeId));

        DemandeCollecteEntity demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de collecte non trouvée avec l'ID : " + demandeId));

        if (demande.getTournee() != null) {
            throw new IllegalStateException("Cette demande est déjà assignée à une tournée");
        }

        demande.setTournee(tournee);
        demandeRepository.save(demande);

        return tourneeMapper.toDto(tourneeRepository.findById(tourneeId).orElse(tournee));
    }

    @Override
    @Transactional
    public TourneeDto removeDemandeFromTournee(UUID tourneeId, UUID demandeId) {
        Tournee tournee = tourneeRepository.findById(tourneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournée non trouvée avec l'ID : " + tourneeId));

        DemandeCollecteEntity demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de collecte non trouvée avec l'ID : " + demandeId));

        if (demande.getTournee() == null || !tournee.getId().equals(demande.getTournee().getId())) {
            throw new IllegalStateException("Cette demande n'est pas assignée à cette tournée");
        }

        demande.setTournee(null);
        demandeRepository.save(demande);

        return tourneeMapper.toDto(tourneeRepository.findById(tourneeId).orElse(tournee));
    }
}
