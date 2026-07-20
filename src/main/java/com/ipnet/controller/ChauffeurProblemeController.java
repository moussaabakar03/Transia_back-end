package com.ipnet.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ipnet.dto.ChauffeurProblemeDto;
import com.ipnet.services.interfaces.ChauffeurProblemeServiceInterface;

@RestController
@RequestMapping("/api/v1/chauffeur-problemes")
@CrossOrigin("*")
public class ChauffeurProblemeController {

    @Autowired
    private ChauffeurProblemeServiceInterface service;

    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody ChauffeurProblemeDto dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            ChauffeurProblemeDto saved = service.save(dto);
            response.put("message", "Problème signalé avec succès.");
            response.put("status", HttpStatus.OK.value());
            response.put("data", saved);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("message", "Erreur lors de l'enregistrement : " + e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ChauffeurProblemeDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/trajet/{trajetId}")
    public ResponseEntity<List<ChauffeurProblemeDto>> getByTrajet(@PathVariable UUID trajetId) {
        return ResponseEntity.ok(service.getByTrajet(trajetId));
    }

    @GetMapping("/chauffeur/{chauffeurId}")
    public ResponseEntity<List<ChauffeurProblemeDto>> getByChauffeur(@PathVariable Long chauffeurId) {
        return ResponseEntity.ok(service.getByChauffeur(chauffeurId));
    }
}