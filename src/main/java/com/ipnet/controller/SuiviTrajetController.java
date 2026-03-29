package com.ipnet.controller;

import com.ipnet.dto.SuiviTrajetDto;
import com.ipnet.services.interfaces.SuiviTrajetServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suivis")
@CrossOrigin("*")
public class SuiviTrajetController {

    @Autowired private SuiviTrajetServiceInterface suiviService;

    @PostMapping("/demarrer/{trajetId}")
    public ResponseEntity<SuiviTrajetDto> demarrer(@PathVariable Long trajetId) {
        return ResponseEntity.ok(suiviService.demarrerSuivi(trajetId));
    }

    @GetMapping("/trajet/{trajetId}")
    public ResponseEntity<SuiviTrajetDto> consulter(@PathVariable Long trajetId) {
        return ResponseEntity.ok(suiviService.getSuiviParTrajet(trajetId));
    }
}