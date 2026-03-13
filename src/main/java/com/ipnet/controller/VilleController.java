package com.ipnet.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.dto.VilleDto;
import com.ipnet.services.interfaces.VilleServiceInterface;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/ville")
public class VilleController {

    private VilleServiceInterface villeService;

    public VilleController(VilleServiceInterface villeService) {
        this.villeService = villeService;
    }

    @PostMapping
    public VilleDto create(@RequestBody VilleDto villeDto) {
        return villeService.create(villeDto);
    }

    @GetMapping
    public List<VilleDto> list() {
        return villeService.listeVille();
    }

    @GetMapping("/{id}")
    public VilleDto get(@PathVariable Long id) {
        return villeService.getVille(id);
    }

    @PutMapping("/{id}")
    public VilleDto update(@RequestBody VilleDto villeDto, @PathVariable Long id) {
        return villeService.update(villeDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        villeService.delete(id);
    }
}