package com.ipnet.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.ipnet.dto.TrajetDto;
import com.ipnet.services.interfaces.TrajetServiceInterface;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/trajet")
public class TrajetController {

    private final TrajetServiceInterface trajetService;

    public TrajetController(TrajetServiceInterface trajetService) {
        this.trajetService = trajetService;
    }

    @PostMapping
    public TrajetDto create(@RequestBody TrajetDto dto) {
        return trajetService.create(dto);
    }

    @GetMapping
    public List<TrajetDto> list() {
        return trajetService.listeTrajet();
    }

    @GetMapping("/{id}")
    public TrajetDto get(@PathVariable Long id) {
        return trajetService.getTrajet(id);
    }

    @PutMapping("/{id}")
    public TrajetDto update(@RequestBody TrajetDto dto, @PathVariable Long id) {
        return trajetService.update(dto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        trajetService.delete(id);
    }
}