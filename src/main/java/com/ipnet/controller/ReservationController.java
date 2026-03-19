package com.ipnet.controller;

import java.util.List;

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
    
    @GetMapping("/trajet/{trajetId}")
    public Integer nombrePlaceTrajet(@PathVariable Long trajetId) {
    	return reservationService.nombrePlaceTrajet(trajetId);
    }
    
    
    @GetMapping
	public List<ReservationResponseDto> listeVehicule() {
		return reservationService.listeReservations();
	}
    
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> modifier(
            @PathVariable Long id, 
            @RequestBody ReservationRequestDto dto) { 
        return ResponseEntity.ok(reservationService.modifierReservation(id, dto));
    }
}