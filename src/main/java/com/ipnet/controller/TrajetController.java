package com.ipnet.controller;

import com.ipnet.dto.TrajetRequestDto;
import com.ipnet.dto.TrajetResponseDto;
import com.ipnet.services.interfaces.TrajetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/trajet")
public class TrajetController {

    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @PostMapping
    public TrajetResponseDto create(@RequestBody TrajetRequestDto request) {
        return trajetService.creerTrajet(request);
    }

    @GetMapping
    public List<TrajetResponseDto> list() {
        return trajetService.listerTousLesTrajets();
    }

    @GetMapping("/{id}")
    public TrajetResponseDto get(@PathVariable UUID id) {
        return trajetService.obtenirTrajet(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        trajetService.supprimerTrajet(id);
    }
}