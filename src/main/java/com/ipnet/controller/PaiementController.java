package com.ipnet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.dto.PaiementRequestDto;
import com.ipnet.entity.PaiementEntity;
import com.ipnet.entity.Reservation;
import com.ipnet.enums.StatutBillet;
import com.ipnet.enums.StatutReservation;
import com.ipnet.services.interfaces.PaiementServiceInterface;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/paiements")
public class PaiementController {

    @Autowired private PaiementServiceInterface paiementService;

    @PostMapping("/caisse")
    public ResponseEntity<String> payerAuGuichet(@RequestBody PaiementRequestDto dto) {
        paiementService.validerPaiementCaisse(dto);
        return ResponseEntity.ok("Paiement encaissé avec succès. Billets confirmés.");
    }
    
    @GetMapping("/caisse")
    public List<PaiementRequestDto> listePaiementCaisse() {
		return paiementService.listePaiementCaisse();
	}
    
    
    @PutMapping("caisse/{id}")
    public PaiementRequestDto update(@RequestBody PaiementRequestDto dto, @PathVariable Long id) {
    	return paiementService.update(dto, id);
    }

    @DeleteMapping
    public void delete(@PathVariable Long id) {
        paiementService.delete(id);
    }

    @GetMapping("/caisse/{id}")
    public PaiementRequestDto getPaiementCaisse(@PathVariable Long id) {
    	return paiementService.getPaiementCaisse(id);
    }
	
    
    
}

