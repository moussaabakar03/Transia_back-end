package com.ipnet.services.interfaces;

import java.util.List;

import com.ipnet.dto.PaiementRequestDto;
import com.ipnet.dto.VehiculeDto;

public interface PaiementServiceInterface {

    public void validerPaiementCaisse(PaiementRequestDto dto);
    public List<PaiementRequestDto> listePaiementCaisse();
    public PaiementRequestDto update(PaiementRequestDto paiementRequestDto, Long id);
	public void delete(Long id);
	public PaiementRequestDto getPaiementCaisse(Long id);
	

}