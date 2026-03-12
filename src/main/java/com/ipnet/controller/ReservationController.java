package com.ipnet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ipnet.dto.*;
import com.ipnet.services.interfaces.ReservationServiceInterface;

@RestController
@RequestMapping("/api/v1/reservations")
@CrossOrigin("*")
public class ReservationController {

    private final ReservationServiceInterface reservationService;

    public ReservationController(ReservationServiceInterface reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDto> create(@RequestBody ReservationRequestDto requestDto) {
        return new ResponseEntity<>(reservationService.create(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }
}