package com.ipnet.controller;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<ReservationResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }
    
    @GetMapping("/trajet/{trajetId}")
    public Integer nombrePlaceTrajet(@PathVariable UUID trajetId) {
    	return reservationService.nombrePlaceTrajet(trajetId);
    }
    
    
    @GetMapping
	public List<ReservationResponseDto> listeVehicule() {
		return reservationService.listeReservations();
	}
    
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> modifier(
            @PathVariable UUID id, 
            @RequestBody ReservationRequestDto dto) { 
        return ResponseEntity.ok(reservationService.modifierReservation(id, dto));
    }
    
    @GetMapping("/trajet/{trajetId}/liste")
    public List<ReservationResponseDto> getReservationsByTrajet(@PathVariable UUID trajetId) {
        return reservationService.getReservationsByTrajet(trajetId);
    }
    
    @GetMapping("/trajet/{trajetId}/sieges-occupes")
    public List<String> getOccupiedSeats(@PathVariable UUID trajetId) {
        return reservationService.getOccupiedSeats(trajetId);
    }
    
}
