package com.ipnet.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ipnet.dto.TourneeDto;
import com.ipnet.dto.TourneeRequestDto;
import com.ipnet.services.interfaces.TourneeServiceInterface;

@RestController
@RequestMapping("/api/v1/tournees")
@CrossOrigin("*")
public class TourneeController {

    private final TourneeServiceInterface tourneeService;

    public TourneeController(TourneeServiceInterface tourneeService) {
        this.tourneeService = tourneeService;
    }

    @PostMapping
    public ResponseEntity<TourneeDto> create(@RequestBody TourneeRequestDto dto) {
        return new ResponseEntity<>(tourneeService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TourneeDto>> listTournees(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) UUID livreurId,
            @RequestParam(required = false) String zone) {
        return ResponseEntity.ok(tourneeService.filterTournees(date, livreurId, zone));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<TourneeDto> getById(@PathVariable UUID publicId) {
        return ResponseEntity.ok(tourneeService.getById(publicId));
    }

    @PatchMapping("/{publicId}")
    public ResponseEntity<TourneeDto> updatePartial(
            @PathVariable UUID publicId,
            @RequestBody TourneeRequestDto dto) {
        return ResponseEntity.ok(tourneeService.updatePartial(publicId, dto));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        tourneeService.delete(publicId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{publicId}/colis/{colisId}")
    public ResponseEntity<TourneeDto> addColisToTournee(
            @PathVariable UUID publicId,
            @PathVariable UUID colisId) {
        return ResponseEntity.ok(tourneeService.addColisToTournee(publicId, colisId));
    }

    @DeleteMapping("/{publicId}/colis/{colisId}")
    public ResponseEntity<TourneeDto> removeColisFromTournee(
            @PathVariable UUID publicId,
            @PathVariable UUID colisId) {
        return ResponseEntity.ok(tourneeService.removeColisFromTournee(publicId, colisId));
    }
}
