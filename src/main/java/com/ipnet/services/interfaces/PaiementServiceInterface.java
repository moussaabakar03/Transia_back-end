package com.ipnet.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.ipnet.dto.PaiementRequestDto;

public interface PaiementServiceInterface {

    public void validerPaiementCaisse(PaiementRequestDto dto);
    public List<PaiementRequestDto> listePaiementCaisse();
    public PaiementRequestDto update(PaiementRequestDto paiementRequestDto, UUID id);
	public void delete(UUID id);
	public PaiementRequestDto getPaiementCaisse(UUID id);
	

}
