package com.ipnet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.dto.PaiementRequestDto;
import com.ipnet.services.interfaces.PaiementServiceInterface;

@RestController
@RequestMapping("/api/v1/paiements")
public class PaiementController {

    @Autowired private PaiementServiceInterface paiementService;

    @PostMapping("/caisse")
    public ResponseEntity<String> payerAuGuichet(@RequestBody PaiementRequestDto dto) {
        paiementService.validerPaiementCaisse(dto);
        return ResponseEntity.ok("Paiement encaissé avec succès. Billets confirmés.");
    }
}

