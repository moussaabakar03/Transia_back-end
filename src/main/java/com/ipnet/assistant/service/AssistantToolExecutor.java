package com.ipnet.assistant.service;

import com.ipnet.dto.AgenceDto;
import com.ipnet.dto.BilletDto;
import com.ipnet.dto.ColisDto;
import com.ipnet.dto.PaiementRequestDto;
import com.ipnet.dto.PositionGpsDto;
import com.ipnet.dto.ReservationResponseDto;
import com.ipnet.dto.SuiviTrajetDto;
import com.ipnet.dto.TrajetResponseDto;
import com.ipnet.dto.VehiculeDto;
import com.ipnet.dto.VilleDto;
import com.ipnet.enums.StatutColis;
import com.ipnet.enums.StatutReservation;
import com.ipnet.enums.StatutTrajet;
import com.ipnet.security.UserDetailsImpl;
import com.ipnet.security.model.Role;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.AgenceServiceInterface;
import com.ipnet.services.interfaces.ColisServiceInterface;
import com.ipnet.services.interfaces.PositionGpsServiceInterface;
import com.ipnet.services.interfaces.ReservationServiceInterface;
import com.ipnet.services.interfaces.SuiviTrajetServiceInterface;
import com.ipnet.services.interfaces.TrajetService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class AssistantToolExecutor {

    private static final ZoneId LOME_ZONE = ZoneId.of("Africa/Lome");
    private static final int MAX_ITEMS = 10;

    private final TrajetService trajetService;
    private final ReservationServiceInterface reservationService;
    private final ColisServiceInterface colisService;
    private final SuiviTrajetServiceInterface suiviTrajetService;
    private final PositionGpsServiceInterface positionGpsService;
    private final AgenceServiceInterface agenceService;
    private final UserRepository userRepository;

    public AssistantToolExecutor(
            TrajetService trajetService,
            ReservationServiceInterface reservationService,
            ColisServiceInterface colisService,
            SuiviTrajetServiceInterface suiviTrajetService,
            PositionGpsServiceInterface positionGpsService,
            AgenceServiceInterface agenceService,
            UserRepository userRepository
    ) {
        this.trajetService = trajetService;
        this.reservationService = reservationService;
        this.colisService = colisService;
        this.suiviTrajetService = suiviTrajetService;
        this.positionGpsService = positionGpsService;
        this.agenceService = agenceService;
        this.userRepository = userRepository;
    }

    public Map<String, Object> execute(
            String toolName,
            Map<String, Object> arguments,
            Authentication authentication
    ) {
        try {
            return switch (toolName) {
                case "search_trips" -> searchTrips(arguments);
                case "get_next_trip" -> getNextTrip(arguments, authentication);
                case "get_my_reservations" -> getMyReservations(arguments, authentication);
                case "get_payment_status" -> getPaymentStatus(arguments, authentication);
                case "get_ticket" -> getTicket(arguments, authentication);
                case "check_refund_eligibility" -> checkRefundEligibility(arguments, authentication);
                case "get_tracking" -> getTracking(arguments, authentication);
                case "get_my_parcels" -> getMyParcels(arguments, authentication);
                case "get_next_parcel" -> getNextParcel(arguments, authentication);
                case "get_chauffeur_passengers" -> getChauffeurPassengers(arguments, authentication);
                case "get_profile" -> getProfile(authentication);
                case "list_agencies" -> listAgencies(arguments);
                case "get_app_instructions" -> getAppInstructions(arguments);
                default -> error(toolName, "Outil inconnu.");
            };
        } catch (RuntimeException exception) {
            return error(
                    toolName,
                    exception.getMessage() == null || exception.getMessage().isBlank()
                            ? "Impossible de lire les données demandées."
                            : exception.getMessage()
            );
        }
    }

    private Map<String, Object> searchTrips(Map<String, Object> arguments) {
        String departure = nullableString(arguments.get("departure"));
        String destination = nullableString(arguments.get("destination"));
        LocalDate date = parseDate(nullableString(arguments.get("date")));
        Integer passengerCount = nullableInteger(arguments.get("passenger_count"));

        LocalDateTime now = LocalDateTime.now(LOME_ZONE);

        List<Map<String, Object>> matches = trajetService.listerTousLesTrajets()
                .stream()
                .filter(trip -> trip.getStatut() != StatutTrajet.ANNULE)
                .filter(trip -> trip.getStatut() != StatutTrajet.TERMINE)
                .filter(trip -> !tripDateTime(trip).isBefore(now))
                .filter(trip -> matchesCity(cityName(trip.getVilleDepart()), departure))
                .filter(trip -> matchesCity(cityName(trip.getVilleArrivee()), destination))
                .filter(trip -> date == null || date.equals(trip.getDateDepart()))
                .map(trip -> tripMap(trip, true))
                .filter(item -> passengerCount == null ||
                        integerValue(item.get("availableSeats"), 0) >= passengerCount)
                .sorted(Comparator.comparing(item -> item.get("departureDateTime").toString()))
                .limit(MAX_ITEMS)
                .toList();

        Map<String, Object> result = success("search_trips");
        result.put("criteria", linkedMap(
                "departure", departure,
                "destination", destination,
                "date", date == null ? null : date.toString(),
                "passengerCount", passengerCount
        ));
        result.put("count", matches.size());
        result.put("trips", matches);
        result.put("recommendedAction", "OPEN_TRIPS");

        if (matches.isEmpty()) {
            result.put("message", "Aucun trajet correspondant n'a été trouvé.");
        }

        return result;
    }

    private Map<String, Object> getNextTrip(
            Map<String, Object> arguments,
            Authentication authentication
    ) {
        User user = currentUser(authentication);
        String perspective = stringValue(arguments.get("perspective"), "auto");
        boolean client = hasRole(authentication, "CLIENT");
        boolean chauffeur = hasRole(authentication, "CHAUFFEUR");

        Map<String, Object> result = success("get_next_trip");
        Map<String, Object> clientTrip = null;
        Map<String, Object> chauffeurTrip = null;

        if (("auto".equals(perspective) || "client".equals(perspective)) && client) {
            ReservationResponseDto nextReservation = selectReservation(
                    reservationService.mesReservations(),
                    null,
                    true
            );
            if (nextReservation != null) {
                clientTrip = reservationMap(nextReservation, true);
            }
        }

        if (("auto".equals(perspective) || "chauffeur".equals(perspective)) && chauffeur) {
            TrajetResponseDto next = trajetService.listerTousLesTrajets()
                    .stream()
                    .filter(trip -> user.getPublicId().equals(trip.getChauffeurId()))
                    .filter(this::isUpcomingTrip)
                    .min(Comparator.comparing(this::tripDateTime))
                    .orElse(null);

            if (next != null) {
                chauffeurTrip = tripMap(next, true);
            }
        }

        result.put("clientTrip", clientTrip);
        result.put("chauffeurTrip", chauffeurTrip);
        result.put("hasResult", clientTrip != null || chauffeurTrip != null);

        if (chauffeurTrip != null && clientTrip == null) {
            result.put("recommendedAction", "OPEN_CHAUFFEUR_TRIP");
        } else if (clientTrip != null) {
            result.put("recommendedAction", "OPEN_RESERVATION");
        }

        return result;
    }

    private Map<String, Object> getMyReservations(
            Map<String, Object> arguments,
            Authentication authentication
    ) {
        requireRole(authentication, "CLIENT");
        String scope = stringValue(arguments.get("scope"), "all");
        LocalDateTime now = LocalDateTime.now(LOME_ZONE);

        List<ReservationResponseDto> all = reservationService.mesReservations();

        List<Map<String, Object>> reservations = all.stream()
                .filter(reservation -> switch (scope) {
                    case "upcoming" -> reservationTripDateTime(reservation) != null &&
                            !reservationTripDateTime(reservation).isBefore(now) &&
                            reservation.getStatut() != StatutReservation.ANNULEE &&
                            reservation.getStatut() != StatutReservation.EXPIREE;
                    case "unpaid" -> reservation.getPaiement() == null &&
                            reservation.getStatut() == StatutReservation.EN_ATTENTE;
                    case "paid" -> reservation.getPaiement() != null ||
                            reservation.getStatut() == StatutReservation.CONFIRMEE;
                    case "cancelled" -> reservation.getStatut() == StatutReservation.ANNULEE ||
                            reservation.getStatut() == StatutReservation.EXPIREE;
                    default -> true;
                })
                .sorted(Comparator.comparing(
                        this::reservationSortDate,
                        Comparator.reverseOrder()
                ))
                .limit(MAX_ITEMS)
                .map(reservation -> reservationMap(reservation, false))
                .toList();

        Map<String, Object> result = success("get_my_reservations");
        result.put("scope", scope);
        result.put("totalCount", all.size());
        result.put("filteredCount", reservations.size());
        result.put("reservations", reservations);
        result.put("recommendedAction", "OPEN_RESERVATIONS");
        return result;
    }

    private Map<String, Object> getPaymentStatus(
            Map<String, Object> arguments,
            Authentication authentication
    ) {
        requireRole(authentication, "CLIENT");
        UUID requestedId = nullableUuid(arguments.get("reservation_id"));
        ReservationResponseDto reservation = selectReservation(
                reservationService.mesReservations(),
                requestedId,
                true
        );

        if (reservation == null) {
            return error("get_payment_status", "Aucune réservation personnelle n'a été trouvée.");
        }

        PaiementRequestDto payment = reservation.getPaiement();
        boolean paid = payment != null || reservation.getStatut() == StatutReservation.CONFIRMEE;

        Map<String, Object> result = success("get_payment_status");
        result.put("reservation", reservationMap(reservation, false));
        result.put("paid", paid);
        result.put("status", paid ? "PAYE" : "NON_PAYE");
        result.put("amount", payment == null ? null : payment.getMontantVerse());
        result.put("reference", payment == null ? null : payment.getReference());
        result.put("paymentMode", payment == null || payment.getModePaiement() == null
                ? null
                : payment.getModePaiement().name());
        result.put("recommendedAction", paid ? "OPEN_TICKET" : "OPEN_RESERVATION");
        return result;
    }

    private Map<String, Object> getTicket(
            Map<String, Object> arguments,
            Authentication authentication
    ) {
        requireRole(authentication, "CLIENT");
        UUID requestedId = nullableUuid(arguments.get("reservation_id"));
        ReservationResponseDto reservation = selectReservation(
                reservationService.mesReservations(),
                requestedId,
                true
        );

        if (reservation == null) {
            return error("get_ticket", "Aucune réservation personnelle n'a été trouvée.");
        }

        List<Map<String, Object>> tickets = reservation.getBillets() == null
                ? List.of()
                : reservation.getBillets().stream()
                .map(this::ticketMap)
                .toList();

        Map<String, Object> result = success("get_ticket");
        result.put("reservationId", reservation.getId().toString());
        result.put("route", routeLabel(reservation.getTrajet()));
        result.put("paid", reservation.getPaiement() != null ||
                reservation.getStatut() == StatutReservation.CONFIRMEE);
        result.put("ticketCount", tickets.size());
        result.put("tickets", tickets);
        result.put("recommendedAction", tickets.isEmpty()
                ? "OPEN_RESERVATION"
                : "OPEN_TICKET");
        return result;
    }

    private Map<String, Object> checkRefundEligibility(
            Map<String, Object> arguments,
            Authentication authentication
    ) {
        requireRole(authentication, "CLIENT");
        UUID requestedId = nullableUuid(arguments.get("reservation_id"));
        ReservationResponseDto reservation = selectReservation(
                reservationService.mesReservations(),
                requestedId,
                true
        );

        if (reservation == null || reservation.getTrajet() == null) {
            return error("check_refund_eligibility", "Aucune réservation personnelle exploitable n'a été trouvée.");
        }

        LocalDateTime departure = reservationTripDateTime(reservation);
        long hours = departure == null
                ? Long.MIN_VALUE
                : Duration.between(LocalDateTime.now(LOME_ZONE), departure).toHours();

        boolean activeStatus = reservation.getStatut() == StatutReservation.EN_ATTENTE ||
                reservation.getStatut() == StatutReservation.CONFIRMEE;
        boolean eligible = activeStatus && hours >= 48;

        Map<String, Object> result = success("check_refund_eligibility");
        result.put("reservation", reservationMap(reservation, false));
        result.put("eligible", eligible);
        result.put("hoursBeforeDeparture", hours == Long.MIN_VALUE ? null : hours);
        result.put("rule", "La demande doit être faite au moins 48 heures avant le départ.");
        result.put("recommendedAction", eligible ? "OPEN_REFUND" : "OPEN_RESERVATION");
        return result;
    }

    private Map<String, Object> getTracking(
            Map<String, Object> arguments,
            Authentication authentication
    ) {
        UUID reservationId = nullableUuid(arguments.get("reservation_id"));
        UUID tripId = nullableUuid(arguments.get("trip_id"));
        User user = currentUser(authentication);

        TrajetResponseDto trip = null;
        ReservationResponseDto reservation = null;

        if (hasRole(authentication, "CLIENT")) {
            reservation = selectReservation(
                    reservationService.mesReservations(),
                    reservationId,
                    true
            );
            if (reservation != null) {
                trip = reservation.getTrajet();
                if (tripId != null && !tripId.equals(reservation.getTrajetId())) {
                    trip = null;
                }
            }
        }

        if (trip == null && hasRole(authentication, "CHAUFFEUR")) {
            final UUID desiredTripId = tripId;
            trip = trajetService.listerTousLesTrajets()
                    .stream()
                    .filter(item -> user.getPublicId().equals(item.getChauffeurId()))
                    .filter(item -> desiredTripId == null || desiredTripId.equals(item.getId()))
                    .filter(this::isUpcomingOrInProgressTrip)
                    .min(Comparator.comparing(this::tripDateTime))
                    .orElse(null);
        }

        if (trip == null) {
            return error("get_tracking", "Aucun trajet autorisé à suivre n'a été trouvé.");
        }

        Map<String, Object> result = success("get_tracking");
        result.put("trip", tripMap(trip, false));
        result.put("reservationId", reservation == null ? null : reservation.getId().toString());

        try {
            SuiviTrajetDto tracking = suiviTrajetService.getSuiviParTrajet(trip.getId());
            result.put("trackingAvailable", true);
            result.put("trackingId", tracking.getId());
            result.put("trackingStatus", tracking.getStatut());
            result.put("message", tracking.getMessage());
            result.put("lastUpdate", tracking.getDerniereMiseAJour());

            PositionGpsDto position = tracking.getDernierePosition();
            if (position == null && tracking.getId() != null) {
                try {
                    position = positionGpsService.getDernierePosition(tracking.getId());
                } catch (RuntimeException ignored) {
                    position = null;
                }
            }
            result.put("position", position == null ? null : positionMap(position));
        } catch (RuntimeException exception) {
            result.put("trackingAvailable", false);
            result.put("trackingStatus", trip.getStatut() == null ? null : trip.getStatut().name());
            result.put("message", "Le suivi GPS n'est pas encore disponible pour ce trajet.");
            result.put("position", null);
        }

        result.put("recommendedAction", "OPEN_TRACKING");
        return result;
    }

    private Map<String, Object> getMyParcels(
            Map<String, Object> arguments,
            Authentication authentication
    ) {
        String perspective = stringValue(arguments.get("perspective"), "auto");
        String status = nullableString(arguments.get("status"));

        List<ColisDto> source = loadAuthorizedParcels(perspective, authentication);
        List<Map<String, Object>> parcels = source.stream()
                .filter(parcel -> status == null || parcel.getStatut() != null &&
                        normalize(parcel.getStatut().name()).contains(normalize(status)))
                .sorted(Comparator.comparing(
                        parcel -> parcel.getDateCreation() == null
                                ? LocalDateTime.MIN
                                : parcel.getDateCreation(),
                        Comparator.reverseOrder()
                ))
                .limit(MAX_ITEMS)
                .map(this::parcelMap)
                .toList();

        Map<String, Object> result = success("get_my_parcels");
        result.put("perspective", perspective);
        result.put("count", parcels.size());
        result.put("parcels", parcels);
        result.put("recommendedAction", "OPEN_PARCELS");
        return result;
    }

    private Map<String, Object> getNextParcel(
            Map<String, Object> arguments,
            Authentication authentication
    ) {
        String perspective = stringValue(arguments.get("perspective"), "auto");
        ColisDto next = loadAuthorizedParcels(perspective, authentication)
                .stream()
                .filter(this::isActiveParcel)
                .min(Comparator.comparing(parcel ->
                        parcel.getDateCreation() == null
                                ? LocalDateTime.MAX
                                : parcel.getDateCreation()))
                .orElse(null);

        if (next == null) {
            return error("get_next_parcel", "Aucun colis actif n'a été trouvé.");
        }

        Map<String, Object> result = success("get_next_parcel");
        result.put("parcel", parcelMap(next));
        result.put("recommendedAction", "OPEN_PARCEL");
        return result;
    }

    private Map<String, Object> getChauffeurPassengers(
            Map<String, Object> arguments,
            Authentication authentication
    ) {
        requireRole(authentication, "CHAUFFEUR");
        User user = currentUser(authentication);
        UUID requestedTripId = nullableUuid(arguments.get("trip_id"));

        TrajetResponseDto trip = trajetService.listerTousLesTrajets()
                .stream()
                .filter(item -> user.getPublicId().equals(item.getChauffeurId()))
                .filter(item -> requestedTripId == null || requestedTripId.equals(item.getId()))
                .filter(this::isUpcomingOrInProgressTrip)
                .min(Comparator.comparing(this::tripDateTime))
                .orElse(null);

        if (trip == null) {
            return error("get_chauffeur_passengers", "Aucun trajet affecté au chauffeur n'a été trouvé.");
        }

        List<ReservationResponseDto> reservations =
                reservationService.getReservationsByTrajet(trip.getId());

        List<Map<String, Object>> passengers = new ArrayList<>();
        int activeReservations = 0;

        for (ReservationResponseDto reservation : reservations) {
            if (reservation.getStatut() == StatutReservation.ANNULEE ||
                    reservation.getStatut() == StatutReservation.EXPIREE) {
                continue;
            }
            activeReservations++;

            if (reservation.getBillets() == null || reservation.getBillets().isEmpty()) {
                passengers.add(linkedMap(
                        "name", reservation.getNomResponsable(),
                        "seat", null,
                        "ticketStatus", null,
                        "reservationId", reservation.getId().toString()
                ));
                continue;
            }

            for (BilletDto ticket : reservation.getBillets()) {
                passengers.add(linkedMap(
                        "name", ticket.getNomPassager(),
                        "seat", ticket.getNumeroSiege(),
                        "ticketStatus", ticket.getStatut() == null ? null : ticket.getStatut().name(),
                        "reservationId", reservation.getId().toString()
                ));
            }
        }

        Map<String, Object> result = success("get_chauffeur_passengers");
        result.put("trip", tripMap(trip, false));
        result.put("activeReservationCount", activeReservations);
        result.put("passengerCount", passengers.size());
        result.put("passengers", passengers.stream().limit(60).toList());
        result.put("recommendedAction", "OPEN_CHAUFFEUR_TRIP");
        return result;
    }

    private Map<String, Object> getProfile(Authentication authentication) {
        User user = currentUser(authentication);

        Set<String> roles = new LinkedHashSet<>();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role != null && role.getName() != null) {
                    roles.add(role.getName().name());
                }
            }
        }

        Map<String, Object> result = success("get_profile");
        result.put("profile", linkedMap(
                "id", user.getPublicId() == null ? null : user.getPublicId().toString(),
                "fullName", user.getNom(),
                "telephone", user.getTelephone(),
                "email", user.getEmail(),
                "accountStatus", user.getStatutCompte() == null ? null : user.getStatutCompte().name(),
                "operationalStatus", user.getStatutOperationnel() == null ? null : user.getStatutOperationnel().name(),
                "roles", new ArrayList<>(roles),
                "agencyId", user.getAgence() == null ? null : user.getAgence().getId().toString(),
                "agencyName", user.getAgence() == null ? null : user.getAgence().getNom(),
                "baseCity", user.getVilleBase() == null ? null : user.getVilleBase().getNomVille(),
                "currentCity", user.getVilleActuelle() == null ? null : user.getVilleActuelle().getNomVille()
        ));
        result.put("recommendedAction", "OPEN_PROFILE");
        return result;
    }

    private Map<String, Object> listAgencies(Map<String, Object> arguments) {
        String city = nullableString(arguments.get("city"));

        List<Map<String, Object>> agencies = agenceService.getAll()
                .stream()
                .filter(agency -> city == null || matchesCity(agency.getVilleNom(), city))
                .filter(agency -> agency.getStatut() == null || agency.getStatut())
                .limit(MAX_ITEMS)
                .map(this::agencyMap)
                .toList();

        Map<String, Object> result = success("list_agencies");
        result.put("city", city);
        result.put("count", agencies.size());
        result.put("agencies", agencies);
        result.put("recommendedAction", "OPEN_AGENCIES");
        return result;
    }

    private Map<String, Object> getAppInstructions(Map<String, Object> arguments) {
        String topic = stringValue(arguments.get("topic"), "general");
        String instructions = switch (topic) {
            case "booking" -> "Ouvrir Accueil, choisir départ, destination et date, sélectionner un trajet, choisir les sièges, vérifier le récapitulatif, confirmer puis payer la totalité pour obtenir le billet QR.";
            case "payment" -> "Ouvrir Réservations, sélectionner une réservation en attente, appuyer sur Payer, vérifier le montant total puis confirmer le paiement.";
            case "ticket" -> "Après paiement confirmé, ouvrir Réservations, sélectionner la réservation payée puis ouvrir le billet QR contenant les passagers et les sièges.";
            case "refund" -> "Ouvrir la réservation concernée puis Demander un remboursement. La demande est admissible lorsqu'elle est envoyée au moins 48 heures avant le départ.";
            case "tracking" -> "Ouvrir Réservations, sélectionner le trajet concerné puis ouvrir le suivi. La position GPS apparaît seulement lorsque le suivi du trajet est actif.";
            case "parcel" -> "Ouvrir la rubrique Colis, créer un envoi, renseigner expéditeur, destinataire et contenu, choisir les agences et le mode de remise, puis suivre le numéro de suivi.";
            case "profile" -> "Ouvrir Profil puis Modifier mon profil pour changer les informations autorisées ou la photo.";
            default -> "Transia permet de rechercher des trajets, réserver, payer, consulter un billet QR, suivre un trajet, demander un remboursement, envoyer des colis et gérer le profil.";
        };

        Map<String, Object> result = success("get_app_instructions");
        result.put("topic", topic);
        result.put("instructions", instructions);
        return result;
    }

    private List<ColisDto> loadAuthorizedParcels(
            String perspective,
            Authentication authentication
    ) {
        boolean client = hasRole(authentication, "CLIENT");
        boolean livreur = hasRole(authentication, "LIVREUR");

        if ("client".equals(perspective)) {
            requireRole(authentication, "CLIENT");
            return colisService.listerMesColis();
        }

        if ("livreur".equals(perspective)) {
            requireRole(authentication, "LIVREUR");
            return colisService.listerMesLivraisons();
        }

        if (livreur && !client) {
            return colisService.listerMesLivraisons();
        }

        if (client && !livreur) {
            return colisService.listerMesColis();
        }

        if (client && livreur) {
            Map<UUID, ColisDto> merged = new LinkedHashMap<>();
            for (ColisDto item : colisService.listerMesColis()) {
                merged.put(item.getId(), item);
            }
            for (ColisDto item : colisService.listerMesLivraisons()) {
                merged.put(item.getId(), item);
            }
            return new ArrayList<>(merged.values());
        }

        throw new IllegalStateException("Ce rôle ne possède pas de colis personnels.");
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Utilisateur non authentifié.");
        }

        if (authentication.getPrincipal() instanceof UserDetailsImpl details) {
            return userRepository.findByPublicId(details.getId())
                    .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable."));
        }

        return userRepository.findByTelephone(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable."));
    }

    private void requireRole(Authentication authentication, String role) {
        if (!hasRole(authentication, role)) {
            throw new IllegalStateException(
                    "Cette information est réservée au rôle " + role + "."
            );
        }
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) {
            return false;
        }
        String expected = "ROLE_" + role.toUpperCase(Locale.ROOT);
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(expected::equals);
    }

    private ReservationResponseDto selectReservation(
            List<ReservationResponseDto> reservations,
            UUID requestedId,
            boolean preferUpcoming
    ) {
        if (requestedId != null) {
            return reservations.stream()
                    .filter(item -> requestedId.equals(item.getId()))
                    .findFirst()
                    .orElse(null);
        }

        if (preferUpcoming) {
            ReservationResponseDto upcoming = reservations.stream()
                    .filter(item -> item.getStatut() != StatutReservation.ANNULEE)
                    .filter(item -> item.getStatut() != StatutReservation.EXPIREE)
                    .filter(item -> reservationTripDateTime(item) != null)
                    .filter(item -> !reservationTripDateTime(item)
                            .isBefore(LocalDateTime.now(LOME_ZONE)))
                    .min(Comparator.comparing(this::reservationTripDateTime))
                    .orElse(null);
            if (upcoming != null) {
                return upcoming;
            }
        }

        return reservations.stream()
                .max(Comparator.comparing(this::reservationSortDate))
                .orElse(null);
    }

    private Map<String, Object> tripMap(
            TrajetResponseDto trip,
            boolean includeAvailableSeats
    ) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", trip.getId() == null ? null : trip.getId().toString());
        result.put("departure", cityName(trip.getVilleDepart()));
        result.put("destination", cityName(trip.getVilleArrivee()));
        result.put("route", routeLabel(trip));
        result.put("date", trip.getDateDepart() == null ? null : trip.getDateDepart().toString());
        result.put("time", trip.getHeureDepart() == null ? null : trip.getHeureDepart().toString());
        result.put("departureDateTime", tripDateTime(trip).toString());
        result.put("status", trip.getStatut() == null ? null : trip.getStatut().name());
        result.put("price", trip.getTarif());
        result.put("distance", trip.getDistance());
        result.put("estimatedDuration", trip.getDureeEstimee());
        result.put("agencyId", trip.getAgenceId() == null ? null : trip.getAgenceId().toString());
        result.put("agencyName", trip.getAgenceNom());
        result.put("driverName", trip.getChauffeurNom());

        VehiculeDto vehicle = trip.getVehicule();
        result.put("vehicle", vehicle == null ? null : linkedMap(
                "id", vehicle.getId() == null ? null : vehicle.getId().toString(),
                "brand", vehicle.getMarque(),
                "model", vehicle.getModele(),
                "registration", vehicle.getImmatriculation(),
                "capacity", vehicle.getCapacite(),
                "status", vehicle.getStatut() == null ? null : vehicle.getStatut().name()
        ));

        if (includeAvailableSeats && trip.getId() != null && vehicle != null) {
            int occupied = reservationService.nombrePlaceTrajet(trip.getId());
            int capacity = vehicle.getCapacite();
            result.put("occupiedSeats", occupied);
            result.put("availableSeats", Math.max(0, capacity - occupied));
        }

        return result;
    }

    private Map<String, Object> reservationMap(
            ReservationResponseDto reservation,
            boolean includeTickets
    ) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", reservation.getId() == null ? null : reservation.getId().toString());
        result.put("status", reservation.getStatut() == null ? null : reservation.getStatut().name());
        result.put("bookingDate", reservation.getDateReservation());
        result.put("seatCount", reservation.getNombrePlace());
        result.put("responsibleName", reservation.getNomResponsable());
        result.put("type", reservation.getTypeReservation() == null
                ? null
                : reservation.getTypeReservation().name());
        result.put("trip", reservation.getTrajet() == null
                ? null
                : tripMap(reservation.getTrajet(), false));

        PaiementRequestDto payment = reservation.getPaiement();
        result.put("paid", payment != null || reservation.getStatut() == StatutReservation.CONFIRMEE);
        result.put("payment", payment == null ? null : linkedMap(
                "id", payment.getId() == null ? null : payment.getId().toString(),
                "amount", payment.getMontantVerse(),
                "reference", payment.getReference(),
                "mode", payment.getModePaiement() == null ? null : payment.getModePaiement().name()
        ));

        if (includeTickets) {
            result.put("tickets", reservation.getBillets() == null
                    ? List.of()
                    : reservation.getBillets().stream().map(this::ticketMap).toList());
        }

        return result;
    }

    private Map<String, Object> ticketMap(BilletDto ticket) {
        return linkedMap(
                "id", ticket.getId() == null ? null : ticket.getId().toString(),
                "passengerName", ticket.getNomPassager(),
                "seat", ticket.getNumeroSiege(),
                "status", ticket.getStatut() == null ? null : ticket.getStatut().name(),
                "qrCode", ticket.getQrCode(),
                "tripId", ticket.getTrajetId() == null ? null : ticket.getTrajetId().toString(),
                "date", ticket.getDateDepart(),
                "time", ticket.getHeureDepart()
        );
    }

    private Map<String, Object> parcelMap(ColisDto parcel) {
        return linkedMap(
                "id", parcel.getId() == null ? null : parcel.getId().toString(),
                "trackingNumber", parcel.getNumeroSuivi(),
                "description", parcel.getDescription(),
                "status", parcel.getStatut() == null ? null : parcel.getStatut().name(),
                "paymentStatus", parcel.getStatutPaiement() == null
                        ? null
                        : parcel.getStatutPaiement().name(),
                "estimatedPrice", parcel.getPrixEstime(),
                "finalPrice", parcel.getPrixFinal(),
                "createdAt", parcel.getDateCreation(),
                "deliveredAt", parcel.getDateLivraison(),
                "departureAgency", parcel.getAgenceDepartNom(),
                "arrivalAgency", parcel.getAgenceArriveeNom(),
                "recipientName", parcel.getDestinataireNom(),
                "recipientAddress", parcel.getDestinataireAdresse(),
                "driverName", parcel.getLivreurNom()
        );
    }

    private Map<String, Object> agencyMap(AgenceDto agency) {
        return linkedMap(
                "id", agency.getId() == null ? null : agency.getId().toString(),
                "name", agency.getNom(),
                "city", agency.getVilleNom(),
                "address", agency.getAdresse(),
                "telephone", agency.getTelephone(),
                "email", agency.getEmail(),
                "latitude", agency.getLatitude(),
                "longitude", agency.getLongitude(),
                "active", agency.getStatut()
        );
    }

    private Map<String, Object> positionMap(PositionGpsDto position) {
        return linkedMap(
                "latitude", position.getLatitude(),
                "longitude", position.getLongitude(),
                "speed", position.getVitesse(),
                "accuracy", position.getPrecisionGps(),
                "altitude", position.getAltitude(),
                "recordedAt", position.getDateHeure()
        );
    }

    private LocalDateTime tripDateTime(TrajetResponseDto trip) {
        LocalDate date = trip.getDateDepart() == null
                ? LocalDate.of(2100, 1, 1)
                : trip.getDateDepart();
        LocalTime time = trip.getHeureDepart() == null
                ? LocalTime.MIDNIGHT
                : trip.getHeureDepart();
        return LocalDateTime.of(date, time);
    }

    private LocalDateTime reservationTripDateTime(ReservationResponseDto reservation) {
        return reservation.getTrajet() == null
                ? null
                : tripDateTime(reservation.getTrajet());
    }

    private LocalDateTime reservationSortDate(ReservationResponseDto reservation) {
        LocalDateTime tripDate = reservationTripDateTime(reservation);
        if (tripDate != null) {
            return tripDate;
        }
        return reservation.getDateReservation() == null
                ? LocalDateTime.MIN
                : reservation.getDateReservation();
    }

    private boolean isUpcomingTrip(TrajetResponseDto trip) {
        return trip.getStatut() != StatutTrajet.ANNULE &&
                trip.getStatut() != StatutTrajet.TERMINE &&
                !tripDateTime(trip).isBefore(LocalDateTime.now(LOME_ZONE));
    }

    private boolean isUpcomingOrInProgressTrip(TrajetResponseDto trip) {
        return trip.getStatut() == StatutTrajet.EN_COURS || isUpcomingTrip(trip);
    }

    private boolean isActiveParcel(ColisDto parcel) {
        StatutColis status = parcel.getStatut();
        return status != StatutColis.LIVRE &&
                status != StatutColis.ANNULE &&
                status != StatutColis.RETOURNE &&
                status != StatutColis.PERDU;
    }

    private String routeLabel(TrajetResponseDto trip) {
        if (trip == null) {
            return "Trajet non renseigné";
        }
        return cityName(trip.getVilleDepart()) + " → " + cityName(trip.getVilleArrivee());
    }

    private String cityName(VilleDto city) {
        return city == null || city.getNomVille() == null
                ? "Ville non renseignée"
                : city.getNomVille();
    }

    private boolean matchesCity(String actual, String expected) {
        if (expected == null || expected.isBlank()) {
            return true;
        }
        return normalize(actual).contains(normalize(expected));
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(
                value.toLowerCase(Locale.ROOT).trim(),
                Normalizer.Form.NFD
        );
        return normalized.replaceAll("\\p{M}", "");
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private UUID nullableUuid(Object value) {
        String text = nullableString(value);
        if (text == null) {
            return null;
        }
        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private String nullableString(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        if (text.isEmpty() || "null".equalsIgnoreCase(text)) {
            return null;
        }
        return text;
    }

    private String stringValue(Object value, String fallback) {
        String text = nullableString(value);
        return text == null ? fallback : text.toLowerCase(Locale.ROOT);
    }

    private Integer nullableInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private int integerValue(Object value, int fallback) {
        Integer result = nullableInteger(value);
        return result == null ? fallback : result;
    }

    private Map<String, Object> success(String tool) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("ok", true);
        result.put("tool", tool);
        return result;
    }

    private Map<String, Object> error(String tool, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("ok", false);
        result.put("tool", tool);
        result.put("message", message);
        return result;
    }

    private Map<String, Object> linkedMap(Object... entries) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < entries.length; index += 2) {
            result.put(entries[index].toString(), entries[index + 1]);
        }
        return result;
    }
}
