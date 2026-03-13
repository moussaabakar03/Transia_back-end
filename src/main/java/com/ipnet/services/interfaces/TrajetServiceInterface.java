package com.ipnet.services.interfaces;

import java.util.List;
import com.ipnet.dto.TrajetDto;

public interface TrajetServiceInterface {
    public TrajetDto create(TrajetDto trajetDto);
    public TrajetDto update(TrajetDto trajetDto, Long id);
    public void delete(Long id);
    public TrajetDto getTrajet(Long id);
    public List<TrajetDto> listeTrajet();
}