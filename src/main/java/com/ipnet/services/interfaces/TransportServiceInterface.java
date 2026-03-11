package com.ipnet.services.interfaces;

import java.util.List;
import com.ipnet.dto.TransportDto;

public interface TransportServiceInterface {
    public TransportDto create(TransportDto transportDto);
    public TransportDto update(TransportDto transportDto, Long id);
    public void delete(Long id);
    public TransportDto getTransport(Long id);
    public List<TransportDto> listeTransport();
}