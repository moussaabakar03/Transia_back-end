package com.ipnet.mappers;

import com.ipnet.dto.TransportDto;
import com.ipnet.entity.TransportEntity;

public class TransportMappers {

    private TransportEntity transportEntity;
    private TransportDto transportDto;

    public TransportMappers(TransportEntity transportEntity, TransportDto transportDto) {
        this.transportDto = transportDto;
        this.transportEntity = transportEntity;
    }

    public TransportEntity toEntity(TransportDto transportDto) {
        if (transportDto == null) {
            return null;
        }
        
        TransportEntity transportEntity = new TransportEntity();
        
        transportEntity.setId(transportDto.getId());
        transportEntity.setVilleDepart(transportDto.getVilleDepart());
        transportEntity.setVilleDestination(transportDto.getVilleDestination());
        transportEntity.setDateHeureDepart(transportDto.getDateHeureDepart());
        transportEntity.setPrix(transportDto.getPrix());
        transportEntity.setStatut(transportDto.getStatut());
        
        
        return transportEntity;
    }

    public TransportDto toDto(TransportEntity transportEntity) {
        if (transportEntity == null) {
            return null;
        }

        TransportDto transportDto = new TransportDto();
        
        transportDto.setId(transportEntity.getId());
        transportDto.setVilleDepart(transportEntity.getVilleDepart());
        transportDto.setVilleDestination(transportEntity.getVilleDestination());
        transportDto.setDateHeureDepart(transportEntity.getDateHeureDepart());
        transportDto.setPrix(transportEntity.getPrix());
        transportDto.setStatut(transportEntity.getStatut());
        
        if (transportEntity.getVehicule() != null) {
            transportDto.setVehiculeId(transportEntity.getVehicule().getId());
        }
        
        return transportDto;
    }
}