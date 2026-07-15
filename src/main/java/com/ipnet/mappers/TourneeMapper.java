package com.ipnet.mappers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipnet.dto.TourneeDto;
import com.ipnet.dto.TourneeRequestDto;
import com.ipnet.entity.Tournee;
import com.ipnet.security.mappers.UserMapper;

@Component
public class TourneeMapper {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired(required = false)
    private ColisMapper colisMapper;

    public TourneeDto toDto(Tournee entity) {
        if (entity == null) {
            return null;
        }

        TourneeDto dto = new TourneeDto();
        dto.setId(entity.getId());
        dto.setDateTournee(entity.getDateTournee());
        dto.setZone(entity.getZone());
        dto.setStatut(entity.getStatut());

        if (entity.getLivreur() != null && userMapper != null) {
            dto.setLivreur(userMapper.mapToUserDTO(entity.getLivreur()));
            dto.setLivreurId(entity.getLivreur().getPublicId());
        }

        if (entity.getColis() != null && colisMapper != null) {
            dto.setColis(entity.getColis().stream()
                    .map(colisMapper::toDto)
                    .toList());
        }

        return dto;
    }

    public List<TourneeDto> toDtoList(List<Tournee> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    public Tournee toEntity(TourneeRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Tournee entity = new Tournee();
        entity.setDateTournee(dto.getDateTournee());
        entity.setZone(dto.getZone());

        return entity;
    }
}
