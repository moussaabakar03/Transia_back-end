package com.ipnet.services.implement;

import com.ipnet.dto.PositionGpsDto;
import com.ipnet.entity.PositionGpsEntity;
import com.ipnet.mappers.PositionGpsMapper;
import com.ipnet.repository.PositionGpsRepository;
import com.ipnet.repository.SuiviTrajetRepository;
import com.ipnet.services.interfaces.PositionGpsServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionGpsServiceImplement implements PositionGpsServiceInterface {

    @Autowired private PositionGpsRepository positionGpsRepository;
    @Autowired private SuiviTrajetRepository suiviTrajetRepository;
    @Autowired private PositionGpsMapper positionGpsMapper;

    @Override
    @Transactional
    public PositionGpsDto enregistrerPosition(PositionGpsDto dto) {
        PositionGpsEntity entity = positionGpsMapper.toEntity(dto);
        
        suiviTrajetRepository.findById(dto.getSuiviTrajetId()).ifPresent(entity::setSuiviTrajet);
        
        PositionGpsEntity saved = positionGpsRepository.save(entity);
        return positionGpsMapper.toDto(saved);
    }

    @Override
    public PositionGpsDto getDernierePosition(Long suiviTrajetId) {
        return positionGpsRepository.findFirstBySuiviTrajetIdOrderByDateHeureDesc(suiviTrajetId)
                .map(positionGpsMapper::toDto)
                .orElse(null);
    }

    @Override
    public List<PositionGpsDto> getHistoriquePositions(Long suiviTrajetId) {
        return positionGpsRepository.findAll().stream() 
                .filter(p -> p.getSuiviTrajet() != null && p.getSuiviTrajet().getId().equals(suiviTrajetId))
                .map(positionGpsMapper::toDto)
                .collect(Collectors.toList());
    }
}