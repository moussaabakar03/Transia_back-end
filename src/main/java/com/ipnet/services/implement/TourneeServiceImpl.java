package com.ipnet.services.implement;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.dto.TourneeDto;
import com.ipnet.dto.TourneeRequestDto;
import com.ipnet.entity.Colis;
import com.ipnet.entity.Tournee;
import com.ipnet.mappers.TourneeMapper;
import com.ipnet.repository.ColisRepository;
import com.ipnet.repository.TourneeRepository;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.TourneeServiceInterface;

@Service
public class TourneeServiceImpl implements TourneeServiceInterface {

    private final TourneeRepository tourneeRepository;
    private final ColisRepository colisRepository;
    private final UserRepository userRepository;
    private final TourneeMapper tourneeMapper;

    public TourneeServiceImpl(
            TourneeRepository tourneeRepository,
            ColisRepository colisRepository,
            UserRepository userRepository,
            TourneeMapper tourneeMapper) {
        this.tourneeRepository = tourneeRepository;
        this.colisRepository = colisRepository;
        this.userRepository = userRepository;
        this.tourneeMapper = tourneeMapper;
    }

    @Override
    @Transactional
    public TourneeDto create(TourneeRequestDto dto) {
        User livreur = userRepository.findByPublicId(dto.getLivreurId())
                .orElseThrow(() -> new RuntimeException("Livreur non trouvé"));

        Tournee tournee = tourneeMapper.toEntity(dto);
        tournee.setLivreur(livreur);
        tournee.setStatut("PLANIFIEE");

        Tournee savedTournee = tourneeRepository.save(tournee);

        // Ajouter les colis à la tournée
        if (dto.getColisIds() != null && !dto.getColisIds().isEmpty()) {
            for (UUID colisId : dto.getColisIds()) {
                addColisToTournee(savedTournee.getId(), colisId);
            }
        }

        return tourneeMapper.toDto(tourneeRepository.findById(savedTournee.getId()).orElse(savedTournee));
    }

    @Override
    public TourneeDto getById(UUID id) {
        Tournee tournee = tourneeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournée non trouvée avec l'ID : " + id));
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
                .orElseThrow(() -> new RuntimeException("Tournée non trouvée avec l'ID : " + id));

        if (dto.getDateTournee() != null) {
            tournee.setDateTournee(dto.getDateTournee());
        }
        if (dto.getZone() != null) {
            tournee.setZone(dto.getZone());
        }
        if (dto.getLivreurId() != null) {
            User livreur = userRepository.findByPublicId(dto.getLivreurId())
                    .orElseThrow(() -> new RuntimeException("Livreur non trouvé"));
            tournee.setLivreur(livreur);
        }

        return tourneeMapper.toDto(tourneeRepository.save(tournee));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Tournee tournee = tourneeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournée non trouvée avec l'ID : " + id));

        // Retirer les colis de la tournée avant suppression
        if (tournee.getColis() != null) {
            for (Colis colis : tournee.getColis()) {
                colis.setTournee(null);
                colisRepository.save(colis);
            }
        }

        tourneeRepository.delete(tournee);
    }

    @Override
    @Transactional
    public TourneeDto addColisToTournee(UUID tourneeId, UUID colisId) {
        Tournee tournee = tourneeRepository.findById(tourneeId)
                .orElseThrow(() -> new RuntimeException("Tournée non trouvée avec l'ID : " + tourneeId));

        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé avec l'ID : " + colisId));

        if (colis.getTournee() != null) {
            throw new RuntimeException("Le colis est déjà assigné à une tournée");
        }

        colis.setTournee(tournee);
        colisRepository.save(colis);

        return tourneeMapper.toDto(tourneeRepository.findById(tourneeId).orElse(tournee));
    }

    @Override
    @Transactional
    public TourneeDto removeColisFromTournee(UUID tourneeId, UUID colisId) {
        Tournee tournee = tourneeRepository.findById(tourneeId)
                .orElseThrow(() -> new RuntimeException("Tournée non trouvée avec l'ID : " + tourneeId));

        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé avec l'ID : " + colisId));

        if (colis.getTournee() == null || !tournee.getId().equals(colis.getTournee().getId())) {
            throw new RuntimeException("Le colis n'est pas assigné à cette tournée");
        }

        colis.setTournee(null);
        colisRepository.save(colis);

        return tourneeMapper.toDto(tourneeRepository.findById(tourneeId).orElse(tournee));
    }
}
