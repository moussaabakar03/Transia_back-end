package com.ipnet.services.implement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ipnet.dto.TransportDto;
import com.ipnet.entity.TransportEntity;
import com.ipnet.entity.VehiculeEntity;
import com.ipnet.mappers.TransportMappers;
import com.ipnet.repository.TransportRepository;
import com.ipnet.repository.VehiculeRepository;
import com.ipnet.services.interfaces.TransportServiceInterface;

@Service
public class TransportServiceImplement implements TransportServiceInterface {

    private TransportRepository transportRepository;
    private VehiculeRepository vehiculeRepository;
    private TransportMappers transportMappers;

    public TransportServiceImplement(TransportRepository transportRepository, 
                                     VehiculeRepository vehiculeRepository,
                                     TransportMappers transportMappers) {
        super();
        this.transportRepository = transportRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.transportMappers = transportMappers;
    }

    @Override
    public TransportDto create(TransportDto transportDto) {
        TransportEntity transportEntity = transportMappers.toEntity(transportDto);
        
        // Liaison avec le véhicule
        if (transportDto.getVehiculeId() != null) {
            VehiculeEntity vehicule = vehiculeRepository.findById(transportDto.getVehiculeId())
                    .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
            transportEntity.setVehicule(vehicule);
        }
        
        TransportEntity transportSave = transportRepository.save(transportEntity);
        return transportMappers.toDto(transportSave);
    }

    @Override
    public TransportDto update(TransportDto transportDto, Long id) {
        // 1. Vérifier que le transport existe
        if (!transportRepository.existsById(id)) {
            throw new RuntimeException("Transport non trouvé avec l'id : " + id);
        }

        // 2. Convertir le DTO en entité
        TransportEntity transport = transportMappers.toEntity(transportDto);
        
        // 3. Forcer l'ID et gérer le véhicule
        transport.setId(id);
        if (transportDto.getVehiculeId() != null) {
            VehiculeEntity vehicule = vehiculeRepository.findById(transportDto.getVehiculeId())
                    .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
            transport.setVehicule(vehicule);
        }

        TransportEntity transportSave = transportRepository.save(transport);
        return transportMappers.toDto(transportSave);
    }

    @Override
    public void delete(Long id) {
        transportRepository.deleteById(id);
    }

    @Override
    public TransportDto getTransport(Long id) {
        TransportEntity rechercheTransport = transportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transport non trouvé"));

        return transportMappers.toDto(rechercheTransport);
    }

    @Override
    public List<TransportDto> listeTransport() {
        List<TransportEntity> transports = transportRepository.findAll();
        ArrayList<TransportDto> transportsDto = new ArrayList<>();

        for (TransportEntity transport : transports) {
            transportsDto.add(transportMappers.toDto(transport));
        }

        return transportsDto;
    }
}