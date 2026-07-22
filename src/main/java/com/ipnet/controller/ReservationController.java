package com.ipnet.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ipnet.dto.ReservationRequestDto;
import com.ipnet.dto.ReservationResponseDto;
import com.ipnet.dto.TrajetResponseDto;
import com.ipnet.security.SecurityUtils;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.ReservationServiceInterface;
import com.ipnet.services.interfaces.TrajetService;

@RestController
@RequestMapping("/api/v1/reservations")
@CrossOrigin("*")
public class ReservationController {

    private final ReservationServiceInterface reservationService;
    private final TrajetService trajetService;
    private final UserRepository userRepository;

    public ReservationController(ReservationServiceInterface reservationService, TrajetService trajetService,
            UserRepository userRepository) {
        this.reservationService = reservationService;
        this.trajetService = trajetService;
        this.userRepository = userRepository;
    }

    // Créer une réservation : ouvert à tout utilisateur connecté. Un CLIENT ne peut réserver que pour
    // lui-même (le service ignore toute identité côté client et résout le titulaire via le JWT) ; le
    // staff crée des réservations "au comptoir" sans compte lié (walk-in, nomResponsable suffit).
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponseDto> create(
            @RequestBody ReservationRequestDto requestDto) {
        return new ResponseEntity<>(reservationService.create(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponseDto> getById(@PathVariable UUID id) {
        ReservationResponseDto dto = reservationService.getById(id);
        checkProprietaireOuStaffAgence(dto);
        return ResponseEntity.ok(dto);
    }

    // Réservations du client connecté — remplace le besoin, côté mobile, de charger toutes les
    // réservations de la plateforme puis de filtrer localement.
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public List<ReservationResponseDto> mesReservations() {
        return reservationService.mesReservations();
    }

    @GetMapping("/trajet/{trajetId}")
    @PreAuthorize("isAuthenticated()")
    public Integer nombrePlaceTrajet(@PathVariable UUID trajetId) {
        return reservationService.nombrePlaceTrajet(trajetId);
    }

    // Liste complète : réservée au staff, filtrée par agence pour tout le monde sauf SUPER_ADMIN.
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','AGENT_ACCUEIL')")
    public List<ReservationResponseDto> listeReservations() {
        List<ReservationResponseDto> toutes = reservationService.listeReservations();
        if (SecurityUtils.hasRole("SUPER_ADMIN")) {
            return toutes;
        }
        UUID agenceId = SecurityUtils.getConnectedUserAgenceId(userRepository);
        return toutes.stream()
                .filter(r -> agenceId != null && agenceId.equals(r.getAgenceId()))
                .toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponseDto> modifier(
            @PathVariable UUID id,
            @RequestBody ReservationRequestDto dto) {
        checkProprietaireOuStaffAgence(reservationService.getById(id));
        return ResponseEntity.ok(reservationService.modifierReservation(id, dto));
    }

    // Réservations d'un trajet donné : staff de l'agence, ou le chauffeur affecté à ce trajet
    // (nécessaire pour la liste d'embarquement / scan QR côté mobile).
    @GetMapping("/trajet/{trajetId}/liste")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','AGENT_ACCUEIL','CHAUFFEUR')")
    public List<ReservationResponseDto> getReservationsByTrajet(@PathVariable UUID trajetId) {
        checkAccesTrajet(trajetId);
        return reservationService.getReservationsByTrajet(trajetId);
    }

    @GetMapping("/trajet/{trajetId}/sieges-occupes")
    @PreAuthorize("isAuthenticated()")
    public List<String> getOccupiedSeats(@PathVariable UUID trajetId) {
        return reservationService.getOccupiedSeats(trajetId);
    }

    @PatchMapping("/{id}/annuler")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> annulerReservation(@PathVariable UUID id) {
        checkProprietaireOuStaffAgence(reservationService.getById(id));
        reservationService.annulerReservation(id);
        return ResponseEntity.ok("Réservation annulée avec succès.");
    }

    /** SUPER_ADMIN : accès libre. ADMIN_AGENCE/AGENT_ACCUEIL : uniquement leur agence. Sinon : le titulaire. */
    private void checkProprietaireOuStaffAgence(ReservationResponseDto reservation) {
        if (SecurityUtils.hasRole("SUPER_ADMIN")) return;

        if (SecurityUtils.hasRole("ADMIN_AGENCE") || SecurityUtils.hasRole("AGENT_ACCUEIL")) {
            SecurityUtils.checkAgenceAccess(userRepository, reservation.getAgenceId());
            return;
        }

        User caller = SecurityUtils.getConnectedUser(userRepository);
        if (reservation.getUserId() == null || !reservation.getUserId().equals(caller.getPublicId())) {
            throw new AccessDeniedException("Vous ne pouvez pas accéder à la réservation d'un autre client");
        }
    }

    /** SUPER_ADMIN : libre. ADMIN_AGENCE/AGENT_ACCUEIL : leur agence. CHAUFFEUR : uniquement s'il est affecté au trajet. */
    private void checkAccesTrajet(UUID trajetId) {
        if (SecurityUtils.hasRole("SUPER_ADMIN")) return;

        TrajetResponseDto trajet = trajetService.obtenirTrajet(trajetId);

        if (SecurityUtils.hasRole("CHAUFFEUR")) {
            User caller = SecurityUtils.getConnectedUser(userRepository);
            if (trajet.getChauffeurId() == null || !trajet.getChauffeurId().equals(caller.getPublicId())) {
                throw new AccessDeniedException("Vous n'êtes pas affecté à ce trajet");
            }
            return;
        }

        SecurityUtils.checkAgenceAccess(userRepository, trajet.getAgenceId());
    }
}
