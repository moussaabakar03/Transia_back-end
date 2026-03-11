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

import com.ipnet.dto.TransportDto;
import com.ipnet.services.interfaces.TransportServiceInterface;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/transport")
public class TransportController {

    private TransportServiceInterface transportService;

    public TransportController(TransportServiceInterface transportService) {
        this.transportService = transportService;
    }

    @PostMapping
    public TransportDto create(@RequestBody TransportDto transportDto) {
        return transportService.create(transportDto);
    }

    @PutMapping("/{id}") 
    public TransportDto update(@RequestBody TransportDto transportDto, @PathVariable Long id) {
        return transportService.update(transportDto, id);
    }

    @DeleteMapping("/{id}") 
    public void delete(@PathVariable Long id) {
        transportService.delete(id);
    }

    @GetMapping("/{id}")
    public TransportDto getTransport(@PathVariable Long id) {
        return transportService.getTransport(id);
    }

    @GetMapping
    public List<TransportDto> listeTransport() {
        return transportService.listeTransport();
    }
}