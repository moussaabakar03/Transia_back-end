package com.ipnet.assistant.service;

import com.ipnet.assistant.dto.AssistantRequest;
import com.ipnet.assistant.dto.AssistantResponse;
import com.ipnet.assistant.model.AssistantIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class AssistantFallbackService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AssistantFallbackService.class);

    private static final ZoneId LOME_ZONE =
            ZoneId.of("Africa/Lome");

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH);

    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("HH'h'mm", Locale.FRENCH);

    private final RestClient restClient;

    private final Map<String, ConversationContext> contexts =
            new ConcurrentHashMap<>();

    public AssistantFallbackService(
            @Value("${transia.internal-api.base-url:http://127.0.0.1:8181}")
            String baseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public AssistantResponse answer(
            AssistantRequest request,
            Authentication authentication,
            String authorizationHeader
    ) {
        String message = request == null || request.getMessage() == null
                ? ""
                : request.getMessage().trim();

        if (message.isEmpty()) {
            return AssistantResponse.simple(
                    AssistantIntent.UNKNOWN.name(),
                    "Écrivez une question pour commencer.",
                    List.of(
                            "Quel est mon prochain trajet ?",
                            "Voir mes réservations"
                    )
            );
        }

        String username = authentication == null
                ? "anonymous"
                : authentication.getName();

        ConversationContext context = contexts.computeIfAbsent(
                username,
                key -> new ConversationContext()
        );

        List<String> cities = loadCities(authorizationHeader);
        Analysis analysis = analyze(message, cities, context);
        context.merge(analysis);

        AssistantResponse response = switch (analysis.intent()) {
            case GREETING -> greeting();
            case HELP -> help();
            case BOOKING_HELP -> bookingHelp();
            case SEARCH_TRIP -> searchTrips(
                    analysis,
                    context,
                    authorizationHeader
            );
            case NEXT_TRIP -> nextTrip(
                    authentication,
                    authorizationHeader,
                    context
            );
            case MY_RESERVATIONS -> reservations(
                    authorizationHeader
            );
            case UNPAID_RESERVATIONS -> unpaidReservations(
                    authorizationHeader
            );
            case PAYMENT_STATUS -> paymentStatus(
                    authorizationHeader,
                    context
            );
            case TICKET -> ticket(
                    authorizationHeader,
                    context
            );
            case REFUND -> refund(
                    authorizationHeader,
                    context
            );
            case TRACKING -> tracking(
                    authentication,
                    authorizationHeader,
                    context
            );
            case MY_PARCELS -> parcels(
                    authorizationHeader
            );
            case NEXT_PARCEL -> nextParcel(
                    authorizationHeader,
                    context
            );
            case PROFILE -> profile(
                    authorizationHeader,
                    authentication
            );
            case THANKS -> thanks();
            case GOODBYE -> goodbye();
            case UNKNOWN -> unknown();
        };

        if (analysis.greeting() &&
                analysis.intent() != AssistantIntent.GREETING) {
            response.setMessage(
                    "Bonjour 👋\n\n" + response.getMessage()
            );
        }

        return response;
    }

    private AssistantResponse greeting() {
        return AssistantResponse.simple(
                AssistantIntent.GREETING.name(),
                "Bonjour 👋 Je suis TransIA. Je peux consulter les trajets, vos réservations, vos paiements, vos billets et vos colis.",
                List.of(
                        "Quels trajets sont disponibles ?",
                        "Quel est mon prochain trajet ?",
                        "Voir mes réservations",
                        "Voir mes colis"
                )
        );
    }

    private AssistantResponse help() {
        return AssistantResponse.simple(
                AssistantIntent.HELP.name(),
                "Vous pouvez me demander : « Y a-t-il un trajet de Lomé vers Kara demain ? », « Ai-je payé ma réservation ? », « Quel est mon prochain trajet ? » ou « Où en est mon colis ? ».",
                List.of(
                        "Trajet Lomé vers Kara demain",
                        "Ai-je payé ma réservation ?",
                        "Quel est mon prochain trajet ?",
                        "Voir mes colis"
                )
        );
    }

    private AssistantResponse bookingHelp() {
        return AssistantResponse.simple(
                AssistantIntent.BOOKING_HELP.name(),
                "Pour réserver un trajet :\n"
                        + "1. Ouvrez l’onglet « Accueil ».\n"
                        + "2. Choisissez la ville de départ, la destination et la date.\n"
                        + "3. Sélectionnez un trajet disponible.\n"
                        + "4. Choisissez le nombre de places et les sièges.\n"
                        + "5. Vérifiez le récapitulatif puis confirmez la réservation.\n"
                        + "6. Effectuez le paiement pour obtenir le billet QR.",
                List.of(
                        "Chercher un trajet",
                        "Voir tous les trajets",
                        "Comment payer ?"
                )
        );
    }

    private AssistantResponse searchTrips(
            Analysis analysis,
            ConversationContext context,
            String authorizationHeader
    ) {
        List<Map<String, Object>> trips = getFirstList(
                authorizationHeader,
                List.of(
                        "/api/v1/trajet",
                        "/api/v1/trajets"
                )
        );

        String departure;
        String destination;
        LocalDate date;

        if (analysis.allTrips()) {
            departure = null;
            destination = null;
            date = null;

            // « Voir tous les trajets » doit supprimer les anciens filtres
            // conservés dans le contexte de conversation.
            context.departure = null;
            context.destination = null;
            context.date = null;
        } else {
            departure = firstNonBlank(
                    analysis.departure(),
                    context.departure
            );

            destination = firstNonBlank(
                    analysis.destination(),
                    context.destination
            );

            date = analysis.date() != null
                    ? analysis.date()
                    : context.date;
        }

        List<Map<String, Object>> matches = trips.stream()
                .filter(item -> cityMatches(
                        cityName(item, true),
                        departure
                ))
                .filter(item -> cityMatches(
                        cityName(item, false),
                        destination
                ))
                .filter(item -> dateMatches(
                        dateTime(item),
                        date
                ))
                .filter(this::isUpcoming)
                .sorted(
                        Comparator.comparing(
                                (Map<String, Object> item) ->
                                        safeDateTime(item, true)
                        )
                )
                .limit(5)
                .toList();

        if (matches.isEmpty()) {
            String criteria = buildCriteria(
                    departure,
                    destination,
                    date
            );

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("departure", nullToEmpty(departure));
            data.put("destination", nullToEmpty(destination));
            data.put("date", date == null ? "" : date.toString());

            return AssistantResponse.action(
                    AssistantIntent.SEARCH_TRIP.name(),
                    "Je n’ai trouvé aucun trajet disponible" +
                            criteria +
                            ". Essayez une autre date ou une autre destination.",
                    List.of(
                            "Voir tous les trajets",
                            "Trajet pour Kara",
                            "Trajet pour Sokodé"
                    ),
                    "OPEN_TRIPS",
                    data
            );
        }

        StringBuilder text = new StringBuilder(
                matches.size() == 1
                        ? "J’ai trouvé ce trajet :"
                        : "J’ai trouvé ces trajets :"
        );

        List<Map<String, Object>> responseTrips = new ArrayList<>();

        for (Map<String, Object> trip : matches) {
            LocalDateTime departureDateTime = dateTime(trip);
            Double price = decimal(
                    trip,
                    "tarif", "prix", "price"
            );
            Integer availableSeats = integer(
                    trip,
                    "placesDisponibles",
                    "seatsAvailable",
                    "nombrePlacesDisponibles"
            );

            text.append("\n• ")
                    .append(routeLabel(trip));

            if (departureDateTime != null) {
                text.append(", le ")
                        .append(departureDateTime.toLocalDate()
                                .format(DISPLAY_DATE))
                        .append(" à ")
                        .append(departureDateTime.toLocalTime()
                                .format(DISPLAY_TIME));
            }

            if (price != null) {
                text.append(", ")
                        .append(formatMoney(price))
                        .append(" FCFA");
            }

            if (availableSeats != null && availableSeats >= 0) {
                text.append(" — ")
                        .append(availableSeats)
                        .append(" place")
                        .append(availableSeats > 1 ? "s" : "")
                        .append(" disponible")
                        .append(availableSeats > 1 ? "s" : "");
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", identifier(trip));
            item.put("route", routeLabel(trip));
            item.put(
                    "dateDepart",
                    departureDateTime == null
                            ? ""
                            : departureDateTime.toLocalDate().toString()
            );
            item.put(
                    "heureDepart",
                    departureDateTime == null
                            ? ""
                            : departureDateTime.toLocalTime().toString()
            );
            item.put("tarif", price);
            item.put("placesDisponibles", availableSeats);
            responseTrips.add(item);
        }

        if (analysis.bookingHelpRequested()) {
            text.append(
                    "\n\nPour réserver, sélectionnez le trajet qui vous convient, "
                            + "choisissez vos sièges puis confirmez la réservation."
            );
        }

        context.lastEntityId = identifier(matches.get(0));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("trajets", responseTrips);
        data.put("departure", nullToEmpty(departure));
        data.put("destination", nullToEmpty(destination));
        data.put("date", date == null ? "" : date.toString());

        return AssistantResponse.action(
                AssistantIntent.SEARCH_TRIP.name(),
                text.toString(),
                List.of(
                        "Voir tous les trajets",
                        "Comment réserver ?",
                        "Combien coûte le premier ?"
                ),
                "OPEN_TRIPS",
                data
        );
    }

    private AssistantResponse nextTrip(
            Authentication authentication,
            String authorizationHeader,
            ConversationContext context
    ) {
        boolean chauffeur = hasRole(
                authentication,
                "CHAUFFEUR"
        );

        List<Map<String, Object>> source = chauffeur
                ? getFirstList(
                        authorizationHeader,
                        List.of(
                                "/api/v1/chauffeur/trajets",
                                "/api/v1/trajet/chauffeur/me",
                                "/api/v1/chauffeurs/me/trajets",
                                "/api/v1/trajet"
                        )
                )
                : getFirstList(
                        authorizationHeader,
                        List.of(
                                "/api/v1/reservations/me",
                                "/api/v1/reservations/client/me",
                                "/api/v1/reservations"
                        )
                );

        Map<String, Object> next = source.stream()
                .filter(this::isUpcoming)
                .min(
                        Comparator.comparing(
                                (Map<String, Object> item) ->
                                        safeDateTime(item, true)
                        )
                )
                .orElse(null);

        if (next == null) {
            return AssistantResponse.simple(
                    AssistantIntent.NEXT_TRIP.name(),
                    chauffeur
                            ? "Aucun trajet à venir ne vous est actuellement affecté."
                            : "Vous n’avez aucun trajet réservé à venir.",
                    chauffeur
                            ? List.of("Actualiser mes trajets")
                            : List.of(
                                    "Chercher un trajet",
                                    "Voir mes réservations"
                            )
            );
        }

        LocalDateTime departureDateTime = dateTime(next);
        String vehicle = string(
                next,
                "vehiculeImmatriculation",
                "immatriculation",
                "vehiculeNom",
                "bus"
        );
        String status = string(
                next,
                "statut",
                "status",
                "statutReservation",
                "statutTrajet"
        );

        StringBuilder text = new StringBuilder(
                chauffeur
                        ? "Votre prochain trajet est "
                        : "Votre prochain voyage réservé est "
        );

        text.append(routeLabel(next));

        if (departureDateTime != null) {
            text.append(", prévu le ")
                    .append(departureDateTime.toLocalDate()
                            .format(DISPLAY_DATE))
                    .append(" à ")
                    .append(departureDateTime.toLocalTime()
                            .format(DISPLAY_TIME));
        }

        if (!vehicle.isBlank()) {
            text.append(". Véhicule : ")
                    .append(vehicle);
        }

        if (!status.isBlank()) {
            text.append(". Statut : ")
                    .append(status);
        }

        text.append(".");

        String id = identifier(next);
        context.lastEntityId = id;

        return AssistantResponse.action(
                AssistantIntent.NEXT_TRIP.name(),
                text.toString(),
                chauffeur
                        ? List.of(
                                "Combien de passagers ?",
                                "Voir mes trajets",
                                "Signaler un problème"
                        )
                        : List.of(
                                "Voir mon billet",
                                "Suivre mon trajet",
                                "Ai-je payé ?"
                        ),
                chauffeur
                        ? "OPEN_CHAUFFEUR_TRIP"
                        : "OPEN_RESERVATION",
                Map.of("id", id)
        );
    }

    private AssistantResponse reservations(
            String authorizationHeader
    ) {
        List<Map<String, Object>> reservations = getFirstList(
                authorizationHeader,
                List.of(
                        "/api/v1/reservations/me",
                        "/api/v1/reservations/client/me",
                        "/api/v1/reservations"
                )
        );

        if (reservations.isEmpty()) {
            return AssistantResponse.simple(
                    AssistantIntent.MY_RESERVATIONS.name(),
                    "Vous n’avez aucune réservation enregistrée.",
                    List.of(
                            "Chercher un trajet",
                            "Comment réserver ?"
                    )
            );
        }

        List<Map<String, Object>> sorted = reservations.stream()
                .sorted(
                        Comparator.comparing(
                                (Map<String, Object> item) ->
                                        safeDateTime(item, false)
                        ).reversed()
                )
                .limit(5)
                .toList();

        StringBuilder text = new StringBuilder(
                "Vous avez " +
                        reservations.size() +
                        " réservation" +
                        (reservations.size() > 1 ? "s" : "") +
                        "."
        );

        for (Map<String, Object> reservation : sorted) {
            LocalDateTime departureDateTime = dateTime(reservation);
            String status = string(
                    reservation,
                    "statut",
                    "status",
                    "statutReservation"
            );

            text.append("\n• ")
                    .append(routeLabel(reservation));

            if (departureDateTime != null) {
                text.append(" — ")
                        .append(departureDateTime.toLocalDate()
                                .format(DISPLAY_DATE))
                        .append(" à ")
                        .append(departureDateTime.toLocalTime()
                                .format(DISPLAY_TIME));
            }

            if (!status.isBlank()) {
                text.append(" — ")
                        .append(status);
            }
        }

        return AssistantResponse.action(
                AssistantIntent.MY_RESERVATIONS.name(),
                text.toString(),
                List.of(
                        "Ai-je payé ma prochaine réservation ?",
                        "Voir mon billet",
                        "Suivre mon trajet"
                ),
                "OPEN_RESERVATIONS",
                Map.of("count", reservations.size())
        );
    }

    private AssistantResponse unpaidReservations(
            String authorizationHeader
    ) {
        List<Map<String, Object>> reservations = getFirstList(
                authorizationHeader,
                List.of(
                        "/api/v1/reservations/me",
                        "/api/v1/reservations/client/me",
                        "/api/v1/reservations"
                )
        );

        if (reservations.isEmpty()) {
            return AssistantResponse.simple(
                    AssistantIntent.UNPAID_RESERVATIONS.name(),
                    "Vous n’avez aucune réservation enregistrée.",
                    List.of(
                            "Chercher un trajet",
                            "Comment réserver ?"
                    )
            );
        }

        List<Map<String, Object>> unpaid = reservations.stream()
                .filter(this::isExplicitlyUnpaid)
                .sorted(
                        Comparator.comparing(
                                (Map<String, Object> item) ->
                                        safeDateTime(item, true)
                        )
                )
                .limit(5)
                .toList();

        if (unpaid.isEmpty()) {
            boolean hasPaymentInformation = reservations.stream()
                    .anyMatch(this::hasPaymentInformation);

            if (!hasPaymentInformation) {
                return AssistantResponse.action(
                        AssistantIntent.UNPAID_RESERVATIONS.name(),
                        "J’ai trouvé vos réservations, mais le serveur ne fournit pas "
                                + "leur statut de paiement. Je ne peux donc pas déterminer "
                                + "lesquelles sont non payées.",
                        List.of(
                                "Voir mes réservations",
                                "Ai-je payé ma prochaine réservation ?"
                        ),
                        "OPEN_RESERVATIONS",
                        Map.of("count", reservations.size())
                );
            }

            return AssistantResponse.action(
                    AssistantIntent.UNPAID_RESERVATIONS.name(),
                    "Vous n’avez aucune réservation explicitement indiquée comme non payée.",
                    List.of(
                            "Voir mes réservations",
                            "Voir mon billet"
                    ),
                    "OPEN_RESERVATIONS",
                    Map.of("count", 0)
            );
        }

        StringBuilder text = new StringBuilder(
                "Vous avez " + unpaid.size() + " réservation"
                        + (unpaid.size() > 1 ? "s" : "")
                        + " non payée"
                        + (unpaid.size() > 1 ? "s" : "")
                        + " :"
        );

        List<String> ids = new ArrayList<>();

        for (Map<String, Object> reservation : unpaid) {
            LocalDateTime departureDateTime = dateTime(reservation);
            String paymentStatus = paymentStatusOf(reservation);

            text.append("\n• ")
                    .append(routeLabel(reservation));

            if (departureDateTime != null) {
                text.append(" — ")
                        .append(departureDateTime.toLocalDate()
                                .format(DISPLAY_DATE))
                        .append(" à ")
                        .append(departureDateTime.toLocalTime()
                                .format(DISPLAY_TIME));
            }

            if (!paymentStatus.isBlank()) {
                text.append(" — ")
                        .append(paymentStatus);
            }

            String id = identifier(reservation);
            if (!id.isBlank()) {
                ids.add(id);
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("count", unpaid.size());
        data.put("reservationIds", ids);

        return AssistantResponse.action(
                AssistantIntent.UNPAID_RESERVATIONS.name(),
                text.toString(),
                List.of(
                        "Comment payer ?",
                        "Voir mes réservations",
                        "Voir la première réservation"
                ),
                "OPEN_RESERVATIONS",
                data
        );
    }

    private AssistantResponse paymentStatus(
            String authorizationHeader,
            ConversationContext context
    ) {
        List<Map<String, Object>> reservations = getFirstList(
                authorizationHeader,
                List.of(
                        "/api/v1/reservations/me",
                        "/api/v1/reservations/client/me",
                        "/api/v1/reservations"
                )
        );

        Map<String, Object> reservation =
                findContextOrNext(reservations, context);

        if (reservation == null) {
            return AssistantResponse.simple(
                    AssistantIntent.PAYMENT_STATUS.name(),
                    "Je n’ai trouvé aucune réservation permettant de vérifier un paiement.",
                    List.of(
                            "Voir mes réservations",
                            "Chercher un trajet"
                    )
            );
        }

        String paymentStatus = string(
                reservation,
                "statutPaiement",
                "paymentStatus",
                "paiementStatut",
                "statusPaiement"
        );

        boolean paid = booleanValue(
                reservation,
                "paye",
                "payee",
                "paid",
                "paiementConfirme"
        ) || containsPaidStatus(paymentStatus);

        Double amount = decimal(
                reservation,
                "montantTotal",
                "montant",
                "total",
                "amount"
        );

        String route = routeLabel(reservation);
        String text;

        if (paid) {
            text = "Le paiement de votre réservation " +
                    route +
                    " est confirmé";
        } else if (!paymentStatus.isBlank()) {
            text = "Le paiement de votre réservation " +
                    route +
                    " a le statut : " +
                    paymentStatus;
        } else {
            text = "Je retrouve votre réservation " +
                    route +
                    ", mais son statut de paiement n’est pas renseigné par le serveur";
        }

        if (amount != null) {
            text += ". Montant : " +
                    formatMoney(amount) +
                    " FCFA";
        }

        text += ".";

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", identifier(reservation));
        data.put("paid", paid);
        data.put("paymentStatus", paymentStatus);

        return AssistantResponse.action(
                AssistantIntent.PAYMENT_STATUS.name(),
                text,
                List.of(
                        "Voir mon billet",
                        "Voir mes réservations",
                        "Demander un remboursement"
                ),
                "OPEN_RESERVATION",
                data
        );
    }

    private AssistantResponse ticket(
            String authorizationHeader,
            ConversationContext context
    ) {
        List<Map<String, Object>> reservations = getFirstList(
                authorizationHeader,
                List.of(
                        "/api/v1/reservations/me",
                        "/api/v1/reservations/client/me",
                        "/api/v1/reservations"
                )
        );

        Map<String, Object> reservation =
                findContextOrNext(reservations, context);

        if (reservation == null) {
            return AssistantResponse.simple(
                    AssistantIntent.TICKET.name(),
                    "Je n’ai trouvé aucune réservation associée à un billet.",
                    List.of(
                            "Voir mes réservations",
                            "Chercher un trajet"
                    )
            );
        }

        String paymentStatus = string(
                reservation,
                "statutPaiement",
                "paymentStatus"
        );

        boolean paid = booleanValue(
                reservation,
                "paye",
                "payee",
                "paid",
                "paiementConfirme"
        ) || containsPaidStatus(paymentStatus);

        String qr = string(
                reservation,
                "qrCode",
                "qrCodeValue",
                "codeQr",
                "ticketCode",
                "numeroBillet"
        );

        String id = identifier(reservation);

        if (!paid && qr.isBlank()) {
            return AssistantResponse.action(
                    AssistantIntent.TICKET.name(),
                    "Votre réservation " +
                            routeLabel(reservation) +
                            " n’a pas encore de billet disponible. Le billet est généré après la confirmation du paiement.",
                    List.of(
                            "Comment payer ?",
                            "Voir mes réservations"
                    ),
                    "OPEN_RESERVATION",
                    Map.of("id", id)
            );
        }

        String text = "Votre billet pour " +
                routeLabel(reservation) +
                " est disponible.";

        if (!qr.isBlank()) {
            text += " Code du billet : " + qr + ".";
        }

        return AssistantResponse.action(
                AssistantIntent.TICKET.name(),
                text,
                List.of(
                        "Ouvrir mon billet",
                        "Suivre mon trajet",
                        "Voir mes réservations"
                ),
                "OPEN_TICKET",
                Map.of("id", id)
        );
    }

    private AssistantResponse refund(
            String authorizationHeader,
            ConversationContext context
    ) {
        List<Map<String, Object>> reservations = getFirstList(
                authorizationHeader,
                List.of(
                        "/api/v1/reservations/me",
                        "/api/v1/reservations/client/me",
                        "/api/v1/reservations"
                )
        );

        Map<String, Object> reservation =
                findContextOrNext(reservations, context);

        if (reservation == null) {
            return AssistantResponse.simple(
                    AssistantIntent.REFUND.name(),
                    "Je n’ai trouvé aucune réservation à examiner pour un remboursement.",
                    List.of("Voir mes réservations")
            );
        }

        LocalDateTime departure = dateTime(reservation);
        String id = identifier(reservation);

        if (departure == null) {
            return AssistantResponse.action(
                    AssistantIntent.REFUND.name(),
                    "Je retrouve votre réservation, mais la date de départ n’est pas disponible. Je ne peux pas vérifier automatiquement le délai de 48 heures.",
                    List.of(
                            "Ouvrir la réservation",
                            "Contacter l’agence"
                    ),
                    "OPEN_RESERVATION",
                    Map.of("id", id)
            );
        }

        long hours = Duration.between(
                LocalDateTime.now(LOME_ZONE),
                departure
        ).toHours();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("eligible", hours >= 48);
        data.put("hoursBeforeDeparture", hours);

        if (hours >= 48) {
            return AssistantResponse.action(
                    AssistantIntent.REFUND.name(),
                    "Oui. Le départ est prévu dans environ " +
                            hours +
                            " heures. Votre demande respecte le délai minimal de 48 heures.",
                    List.of(
                            "Demander un remboursement",
                            "Voir la réservation"
                    ),
                    "OPEN_REFUND",
                    data
            );
        }

        String text = hours >= 0
                ? "Non. Le départ est prévu dans environ " +
                hours +
                " heures, soit moins que le délai minimal de 48 heures."
                : "Ce trajet est déjà passé. La demande automatique de remboursement n’est plus disponible.";

        return AssistantResponse.action(
                AssistantIntent.REFUND.name(),
                text,
                List.of(
                        "Voir la réservation",
                        "Contacter l’agence"
                ),
                "OPEN_RESERVATION",
                data
        );
    }

    private AssistantResponse tracking(
            Authentication authentication,
            String authorizationHeader,
            ConversationContext context
    ) {
        boolean chauffeur = hasRole(
                authentication,
                "CHAUFFEUR"
        );

        List<Map<String, Object>> source = chauffeur
                ? getFirstList(
                        authorizationHeader,
                        List.of(
                                "/api/v1/chauffeur/trajets",
                                "/api/v1/trajet/chauffeur/me",
                                "/api/v1/chauffeurs/me/trajets",
                                "/api/v1/trajet"
                        )
                )
                : getFirstList(
                        authorizationHeader,
                        List.of(
                                "/api/v1/reservations/me",
                                "/api/v1/reservations/client/me",
                                "/api/v1/reservations"
                        )
                );

        Map<String, Object> item =
                findContextOrNext(source, context);

        if (item == null) {
            return AssistantResponse.simple(
                    AssistantIntent.TRACKING.name(),
                    "Aucun trajet à suivre n’a été trouvé.",
                    chauffeur
                            ? List.of("Voir mes trajets")
                            : List.of(
                                    "Voir mes réservations",
                                    "Chercher un trajet"
                            )
            );
        }

        String status = string(
                item,
                "statutTrajet",
                "statut",
                "status",
                "etat"
        );

        String vehicle = string(
                item,
                "vehiculeImmatriculation",
                "immatriculation",
                "vehiculeNom"
        );

        Double latitude = decimal(
                item,
                "latitude",
                "lat"
        );

        Double longitude = decimal(
                item,
                "longitude",
                "lng",
                "lon"
        );

        StringBuilder text = new StringBuilder(
                "Le trajet " + routeLabel(item)
        );

        if (!status.isBlank()) {
            text.append(" a le statut ")
                    .append(status);
        } else {
            text.append(" est bien enregistré");
        }

        if (!vehicle.isBlank()) {
            text.append(". Véhicule : ")
                    .append(vehicle);
        }

        if (latitude != null && longitude != null) {
            text.append(". Une position GPS est disponible");
        } else {
            text.append(". Aucune position GPS en temps réel n’est actuellement fournie par le serveur");
        }

        text.append(".");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", identifier(item));
        data.put("latitude", latitude == null ? "" : latitude);
        data.put("longitude", longitude == null ? "" : longitude);

        return AssistantResponse.action(
                AssistantIntent.TRACKING.name(),
                text.toString(),
                chauffeur
                        ? List.of(
                                "Voir le trajet",
                                "Voir les passagers"
                        )
                        : List.of(
                                "Ouvrir le suivi",
                                "Voir mon billet"
                        ),
                chauffeur
                        ? "OPEN_CHAUFFEUR_TRIP"
                        : "OPEN_TRACKING",
                data
        );
    }

    private AssistantResponse parcels(
            String authorizationHeader
    ) {
        List<Map<String, Object>> parcels = getFirstList(
                authorizationHeader,
                List.of(
                        "/api/v1/colis/me",
                        "/api/v1/colis/client/me",
                        "/api/v1/colis/livreur/me",
                        "/api/v1/colis"
                )
        );

        if (parcels.isEmpty()) {
            return AssistantResponse.simple(
                    AssistantIntent.MY_PARCELS.name(),
                    "Aucun colis n’est actuellement associé à votre compte.",
                    List.of(
                            "Envoyer un colis",
                            "Voir les agences"
                    )
            );
        }

        Map<String, Long> counts = parcels.stream()
                .collect(Collectors.groupingBy(
                        item -> {
                            String status = string(
                                    item,
                                    "statut",
                                    "status",
                                    "etat"
                            );
                            return status.isBlank()
                                    ? "NON RENSEIGNÉ"
                                    : status;
                        },
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        String details = counts.entrySet()
                .stream()
                .map(entry -> entry.getKey() +
                        " : " +
                        entry.getValue())
                .collect(Collectors.joining(", "));

        return AssistantResponse.action(
                AssistantIntent.MY_PARCELS.name(),
                "Vous avez " +
                        parcels.size() +
                        " colis. Répartition : " +
                        details +
                        ".",
                List.of(
                        "Quel est mon prochain colis ?",
                        "Voir mes colis",
                        "Envoyer un colis"
                ),
                "OPEN_PARCELS",
                Map.of("count", parcels.size())
        );
    }

    private AssistantResponse nextParcel(
            String authorizationHeader,
            ConversationContext context
    ) {
        List<Map<String, Object>> parcels = getFirstList(
                authorizationHeader,
                List.of(
                        "/api/v1/colis/me",
                        "/api/v1/colis/client/me",
                        "/api/v1/colis/livreur/me",
                        "/api/v1/colis"
                )
        );

        Map<String, Object> next = parcels.stream()
                .filter(item -> {
                    String status = string(
                            item,
                            "statut",
                            "status",
                            "etat"
                    ).toUpperCase(Locale.ROOT);

                    return !status.contains("LIVRE") &&
                            !status.contains("ANNULE") &&
                            !status.contains("TERMINE");
                })
                .min(
                        Comparator.comparing(
                                (Map<String, Object> item) ->
                                        safeDateTime(item, true)
                        )
                )
                .orElse(null);

        if (next == null) {
            return AssistantResponse.simple(
                    AssistantIntent.NEXT_PARCEL.name(),
                    "Aucun colis à traiter prochainement n’a été trouvé.",
                    List.of(
                            "Voir mes colis",
                            "Envoyer un colis"
                    )
            );
        }

        String reference = string(
                next,
                "reference",
                "code",
                "numeroColis"
        );

        String status = string(
                next,
                "statut",
                "status",
                "etat"
        );

        String destination = string(
                next,
                "villeDestinationNom",
                "destinationNom",
                "destination"
        );

        String id = identifier(next);
        context.lastEntityId = id;

        String text = "Votre prochain colis" +
                (reference.isBlank() ? "" : " " + reference) +
                " a le statut " +
                (status.isBlank() ? "non renseigné" : status) +
                (destination.isBlank()
                        ? ""
                        : " et doit aller vers " + destination) +
                ".";

        return AssistantResponse.action(
                AssistantIntent.NEXT_PARCEL.name(),
                text,
                List.of(
                        "Ouvrir le colis",
                        "Voir mes colis"
                ),
                "OPEN_PARCEL",
                Map.of("id", id)
        );
    }

    private AssistantResponse profile(
            String authorizationHeader,
            Authentication authentication
    ) {
        Map<String, Object> profile = getFirstMap(
                authorizationHeader,
                List.of(
                        "/api/v1/me",
                        "/api/v1/profil/me"
                )
        );

        String name = string(
                profile,
                "fullName",
                "nomComplet",
                "name",
                "nom"
        );

        String phone = string(
                profile,
                "telephone",
                "phone"
        );

        String agency = string(
                profile,
                "agenceNom",
                "nomAgence"
        );

        String city = string(
                profile,
                "villeNom",
                "villeBaseNom",
                "villeActuelleNom"
        );

        List<String> roles = roles(authentication);
        StringBuilder text = new StringBuilder();

        if (!name.isBlank()) {
            text.append("Votre profil est enregistré au nom de ")
                    .append(name)
                    .append(".");
        } else {
            text.append("Votre profil est bien authentifié.");
        }

        if (!phone.isBlank()) {
            text.append(" Téléphone : ")
                    .append(phone)
                    .append(".");
        }

        if (!roles.isEmpty()) {
            text.append(" Rôle : ")
                    .append(String.join(", ", roles))
                    .append(".");
        }

        if (!agency.isBlank()) {
            text.append(" Agence : ")
                    .append(agency)
                    .append(".");
        }

        if (!city.isBlank()) {
            text.append(" Ville : ")
                    .append(city)
                    .append(".");
        }

        return AssistantResponse.action(
                AssistantIntent.PROFILE.name(),
                text.toString(),
                List.of(
                        "Modifier mon profil",
                        "Voir mes réservations"
                ),
                "OPEN_PROFILE",
                Map.of()
        );
    }

    private AssistantResponse thanks() {
        return AssistantResponse.simple(
                AssistantIntent.THANKS.name(),
                "Avec plaisir 😊 Je reste disponible.",
                List.of(
                        "Quel est mon prochain trajet ?",
                        "Voir mes réservations"
                )
        );
    }

    private AssistantResponse goodbye() {
        return AssistantResponse.simple(
                AssistantIntent.GOODBYE.name(),
                "Au revoir 👋 Bon voyage avec Transia.",
                List.of()
        );
    }

    private AssistantResponse unknown() {
        return AssistantResponse.simple(
                AssistantIntent.UNKNOWN.name(),
                "Je n’ai pas encore assez d’informations pour répondre précisément. Essayez : « Y a-t-il un trajet de Lomé vers Kara demain ? » ou « Ai-je payé ma prochaine réservation ? ».",
                List.of(
                        "Trajet Lomé vers Kara demain",
                        "Quel est mon prochain trajet ?",
                        "Ai-je payé ?",
                        "Voir mes colis"
                )
        );
    }

    private Analysis analyze(
            String message,
            List<String> cities,
            ConversationContext context
    ) {
        String text = normalize(message);

        boolean greeting = containsAny(
                text,
                "bonjour",
                "salut",
                "bonsoir",
                "hello",
                "coucou",
                "comment vas tu",
                "comment tu vas",
                "ca va"
        );

        boolean bookingHelpRequested = containsAny(
                text,
                "comment reserver",
                "comment faire pour reserver",
                "comment faire une reservation",
                "comment effectuer une reservation",
                "les etapes pour reserver",
                "etapes pour reserver",
                "procedure de reservation",
                "processus de reservation",
                "que faire pour reserver",
                "je veux savoir comment reserver",
                "comment acheter un billet"
        );

        boolean allTrips = containsAny(
                text,
                "voir tous les trajets",
                "afficher tous les trajets",
                "liste de tous les trajets",
                "tous les trajets",
                "voir les trajets disponibles",
                "quels trajets sont disponibles",
                "montre moi les trajets",
                "affiche les trajets"
        );

        boolean unpaidReservationsRequested = containsAny(
                text,
                "reservations non payees",
                "reservation non payee",
                "reservations impayees",
                "reservation impayee",
                "reservations pas payees",
                "reservation pas payee",
                "reservations en attente de paiement",
                "reservation en attente de paiement",
                "ai je des reservations non payees",
                "quelles reservations ne sont pas payees"
        );

        Map<AssistantIntent, Integer> scores = new LinkedHashMap<>();

        if (bookingHelpRequested) {
            scores.merge(
                    AssistantIntent.BOOKING_HELP,
                    20,
                    Integer::sum
            );
        }

        if (allTrips) {
            scores.merge(
                    AssistantIntent.SEARCH_TRIP,
                    18,
                    Integer::sum
            );
        }

        if (unpaidReservationsRequested) {
            scores.merge(
                    AssistantIntent.UNPAID_RESERVATIONS,
                    22,
                    Integer::sum
            );
        }

        addScore(scores, AssistantIntent.TRACKING, text, 10,
                "suivre mon trajet",
                "suivre le trajet",
                "suivi de mon trajet",
                "localiser le bus",
                "ou est mon bus",
                "position du bus",
                "trajet en temps reel",
                "gps",
                "le suivre");

        addScore(scores, AssistantIntent.NEXT_TRIP, text, 9,
                "mon prochain trajet",
                "prochain trajet",
                "prochain voyage",
                "mon prochain voyage",
                "quel trajet ai je",
                "trajet qui m est affecte");

        addScore(scores, AssistantIntent.SEARCH_TRIP, text, 8,
                "y a t il un trajet",
                "chercher un trajet",
                "trouver un trajet",
                "trajet disponible",
                "trajets disponibles",
                "horaire",
                "horaires",
                "combien coute",
                "quel est le prix",
                "tarif",
                "reserver un trajet",
                "reserver une place");

        addScore(scores, AssistantIntent.PAYMENT_STATUS, text, 9,
                "ai je paye",
                "est ce que j ai paye",
                "paiement confirme",
                "paiement en attente",
                "statut du paiement",
                "mon paiement",
                "combien ai je paye",
                "paiement refuse",
                "comment payer",
                "comment effectuer le paiement",
                "payer ma reservation");

        addScore(scores, AssistantIntent.TICKET, text, 9,
                "mon billet",
                "voir mon billet",
                "billet electronique",
                "qr code",
                "code qr",
                "ticket",
                "ou est mon billet",
                "comment obtenir mon billet");

        addScore(scores, AssistantIntent.REFUND, text, 9,
                "remboursement",
                "rembourser",
                "annuler ma reservation",
                "annuler mon billet",
                "recuperer mon argent",
                "puis je annuler",
                "comment annuler");

        addScore(scores, AssistantIntent.MY_RESERVATIONS, text, 8,
                "mes reservations",
                "ma reservation",
                "voir mes reservations",
                "reservation en cours",
                "reservations en attente",
                "j ai reserve",
                "deja reserve",
                "combien de reservations ai je");

        addScore(scores, AssistantIntent.NEXT_PARCEL, text, 9,
                "mon prochain colis",
                "prochaine livraison",
                "prochain colis",
                "colis a livrer aujourd hui",
                "prochaine collecte");

        addScore(scores, AssistantIntent.MY_PARCELS, text, 8,
                "mes colis",
                "voir mes colis",
                "suivre mon colis",
                "statut de mon colis",
                "livraisons en attente",
                "envoyer un colis",
                "colis");

        addScore(scores, AssistantIntent.PROFILE, text, 8,
                "mon profil",
                "mon compte",
                "mes informations",
                "modifier mon profil",
                "changer ma photo");

        addScore(scores, AssistantIntent.HELP, text, 7,
                "aide",
                "aidez moi",
                "comment ca marche",
                "que peux tu faire",
                "besoin d aide");

        addScore(scores, AssistantIntent.THANKS, text, 6,
                "merci",
                "je te remercie",
                "thanks");

        addScore(scores, AssistantIntent.GOODBYE, text, 6,
                "au revoir",
                "a bientot",
                "bonne journee",
                "bonne soiree",
                "bye");

        if (greeting) {
            scores.merge(
                    AssistantIntent.GREETING,
                    3,
                    Integer::sum
            );
        }

        if (containsWord(text, "suivre") ||
                containsWord(text, "localiser")) {
            scores.merge(
                    AssistantIntent.TRACKING,
                    5,
                    Integer::sum
            );
        }

        CityPair pair = extractCities(text, cities);
        LocalDate date = extractDate(text);
        Integer passengerCount = extractPassengerCount(text);

        boolean explicitTripQuestion =
                containsAny(
                        text,
                        "y a t il un trajet",
                        "trajet disponible",
                        "trajets disponibles",
                        "chercher un trajet",
                        "trouver un trajet"
                ) ||
                (pair.departure != null && pair.destination != null);

        AssistantIntent intent = scores.entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(AssistantIntent.UNKNOWN);

        // Une phrase peut demander à la fois comment réserver et si un trajet
        // précis existe. Dans ce cas, on cherche d’abord le trajet puis on
        // ajoute les étapes de réservation dans la réponse.
        if (bookingHelpRequested && explicitTripQuestion) {
            intent = AssistantIntent.SEARCH_TRIP;
        }

        if (intent == AssistantIntent.UNKNOWN &&
                context.lastIntent != null &&
                isFollowUp(text)) {
            intent = context.lastIntent;
        }

        if (intent == AssistantIntent.SEARCH_TRIP && !allTrips) {
            if (pair.departure == null) {
                pair.departure = context.departure;
            }

            if (pair.destination == null) {
                pair.destination = context.destination;
            }

            if (date == null) {
                date = context.date;
            }
        }

        return new Analysis(
                intent,
                greeting,
                pair.departure,
                pair.destination,
                date,
                passengerCount,
                allTrips,
                bookingHelpRequested
        );
    }


    private List<String> loadCities(
            String authorizationHeader
    ) {
        List<Map<String, Object>> cityMaps = getFirstList(
                authorizationHeader,
                List.of(
                        "/api/v1/ville",
                        "/api/v1/villes"
                )
        );

        List<String> cities = cityMaps.stream()
                .map(item -> string(
                        item,
                        "nomVille",
                        "nom",
                        "name",
                        "libelle"
                ))
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();

        if (!cities.isEmpty()) {
            return cities;
        }

        return List.of(
                "Lomé",
                "Kpalimé",
                "Sokodé",
                "Kara"
        );
    }

    private List<Map<String, Object>> getFirstList(
            String authorizationHeader,
            List<String> paths
    ) {
        for (String path : paths) {
            Object response = get(
                    authorizationHeader,
                    path
            );

            List<Map<String, Object>> values = toMapList(response);

            if (!values.isEmpty()) {
                return values;
            }
        }

        return new ArrayList<>();
    }

    private Map<String, Object> getFirstMap(
            String authorizationHeader,
            List<String> paths
    ) {
        for (String path : paths) {
            Object response = get(
                    authorizationHeader,
                    path
            );

            if (response instanceof Map<?, ?> rawMap) {
                return toStringMap(rawMap);
            }
        }

        return Map.of();
    }

    private Object get(
            String authorizationHeader,
            String path
    ) {
        try {
            RestClient.RequestHeadersSpec<?> request =
                    restClient.get().uri(path);

            if (authorizationHeader != null &&
                    !authorizationHeader.isBlank()) {
                request = request.header(
                        HttpHeaders.AUTHORIZATION,
                        authorizationHeader
                );
            }

            return request
                    .retrieve()
                    .body(Object.class);
        } catch (RestClientException exception) {
            LOGGER.debug(
                    "Endpoint assistant indisponible {} : {}",
                    path,
                    exception.getMessage()
            );
            return null;
        }
    }

    private List<Map<String, Object>> toMapList(Object value) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (value instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> rawMap) {
                    result.add(toStringMap(rawMap));
                }
            }
            return result;
        }

        if (value instanceof Map<?, ?> rawMap) {
            Map<String, Object> map = toStringMap(rawMap);

            for (String key : List.of(
                    "content",
                    "data",
                    "items",
                    "results",
                    "trajets",
                    "reservations",
                    "colis",
                    "villes"
            )) {
                Object nested = map.get(key);

                if (nested instanceof List<?>) {
                    return toMapList(nested);
                }
            }

            result.add(map);
        }

        return result;
    }

    private Map<String, Object> toStringMap(
            Map<?, ?> rawMap
    ) {
        Map<String, Object> result = new LinkedHashMap<>();

        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            if (entry.getKey() != null) {
                result.put(
                        entry.getKey().toString(),
                        entry.getValue()
                );
            }
        }

        return result;
    }

    private Optional<Object> findFirst(
            Object root,
            String... keys
    ) {
        if (root == null || keys.length == 0) {
            return Optional.empty();
        }

        Deque<Object> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Object current = queue.removeFirst();

            if (current instanceof Map<?, ?> rawMap) {
                Map<String, Object> map = toStringMap(rawMap);

                for (String key : keys) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().equalsIgnoreCase(key) &&
                                entry.getValue() != null) {
                            return Optional.of(entry.getValue());
                        }
                    }
                }

                for (Object nested : map.values()) {
                    if (nested instanceof Map<?, ?> ||
                            nested instanceof Collection<?>) {
                        queue.addLast(nested);
                    }
                }
            } else if (current instanceof Collection<?> collection) {
                for (Object nested : collection) {
                    if (nested != null) {
                        queue.addLast(nested);
                    }
                }
            }
        }

        return Optional.empty();
    }

    private String string(
            Object root,
            String... keys
    ) {
        return findFirst(root, keys)
                .map(Object::toString)
                .map(String::trim)
                .orElse("");
    }

    private Integer integer(
            Object root,
            String... keys
    ) {
        Optional<Object> value = findFirst(root, keys);

        if (value.isEmpty()) {
            return null;
        }

        Object raw = value.get();

        if (raw instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.parseInt(raw.toString());
        } catch (NumberFormatException ignored) {
            try {
                return (int) Double.parseDouble(raw.toString());
            } catch (NumberFormatException secondIgnored) {
                return null;
            }
        }
    }

    private Double decimal(
            Object root,
            String... keys
    ) {
        Optional<Object> value = findFirst(root, keys);

        if (value.isEmpty()) {
            return null;
        }

        Object raw = value.get();

        if (raw instanceof Number number) {
            return number.doubleValue();
        }

        try {
            return Double.parseDouble(raw.toString());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private boolean booleanValue(
            Object root,
            String... keys
    ) {
        Optional<Object> value = findFirst(root, keys);

        if (value.isEmpty()) {
            return false;
        }

        Object raw = value.get();

        if (raw instanceof Boolean bool) {
            return bool;
        }

        String text = raw.toString()
                .trim()
                .toUpperCase(Locale.ROOT);

        return text.equals("TRUE") ||
                text.equals("OUI") ||
                text.contains("PAYE") ||
                text.contains("PAID") ||
                text.contains("CONFIRME") ||
                text.contains("VALIDE");
    }

    private LocalDateTime dateTime(Object root) {
        String dateText = string(
                root,
                "dateDepart",
                "date",
                "jourDepart",
                "dateLivraison",
                "dateCollecte"
        );

        String timeText = string(
                root,
                "heureDepart",
                "heure",
                "time",
                "heureLivraison",
                "heureCollecte"
        );

        LocalDate date = parseDate(dateText);

        if (date == null) {
            return null;
        }

        LocalTime time = parseTime(timeText);

        return LocalDateTime.of(
                date,
                time == null ? LocalTime.MIDNIGHT : time
        );
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String clean = value.trim();

        if (clean.length() >= 10) {
            clean = clean.substring(0, 10);
        }

        for (DateTimeFormatter formatter : List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("dd/MM/uuuu"),
                DateTimeFormatter.ofPattern("d/M/uuuu")
        )) {
            try {
                return LocalDate.parse(clean, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String clean = value.trim();

        for (DateTimeFormatter formatter : List.of(
                DateTimeFormatter.ISO_LOCAL_TIME,
                DateTimeFormatter.ofPattern("HH:mm:ss"),
                DateTimeFormatter.ofPattern("HH:mm")
        )) {
            try {
                return LocalTime.parse(clean, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    private String cityName(
            Object root,
            boolean departure
    ) {
        if (departure) {
            String direct = string(
                    root,
                    "villeDepartNom",
                    "departNom",
                    "fromCity"
            );

            if (!direct.isBlank()) {
                return direct;
            }

            Object nested = findFirst(
                    root,
                    "villeDepart"
            ).orElse(null);

            if (nested instanceof String text) {
                return text;
            }

            return string(
                    nested,
                    "nomVille",
                    "nom",
                    "name",
                    "libelle"
            );
        }

        String direct = string(
                root,
                "villeArriveeNom",
                "arriveeNom",
                "toCity"
        );

        if (!direct.isBlank()) {
            return direct;
        }

        Object nested = findFirst(
                root,
                "villeArrivee"
        ).orElse(null);

        if (nested instanceof String text) {
            return text;
        }

        return string(
                nested,
                "nomVille",
                "nom",
                "name",
                "libelle"
        );
    }

    private String routeLabel(Object root) {
        String direct = string(
                root,
                "trajetLabel",
                "routeLabel",
                "libelleTrajet"
        );

        if (!direct.isBlank()) {
            return direct;
        }

        String departure = cityName(root, true);
        String destination = cityName(root, false);

        if (!departure.isBlank() || !destination.isBlank()) {
            return (departure.isBlank() ? "Départ" : departure) +
                    " → " +
                    (destination.isBlank() ? "Arrivée" : destination);
        }

        return "Trajet";
    }

    private String identifier(Object root) {
        return string(
                root,
                "publicId",
                "id",
                "uuid",
                "reservationId",
                "trajetId",
                "colisId"
        );
    }

    private Map<String, Object> findContextOrNext(
            List<Map<String, Object>> items,
            ConversationContext context
    ) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        if (context.lastEntityId != null &&
                !context.lastEntityId.isBlank()) {
            for (Map<String, Object> item : items) {
                if (context.lastEntityId.equals(identifier(item))) {
                    return item;
                }
            }
        }

        return items.stream()
                .filter(this::isUpcoming)
                .min(
                        Comparator.comparing(
                                (Map<String, Object> item) ->
                                        safeDateTime(item, true)
                        )
                )
                .orElse(items.get(0));
    }

    private boolean isUpcoming(Map<String, Object> item) {
        LocalDateTime value = dateTime(item);

        if (value == null) {
            return true;
        }

        return !value.isBefore(
                LocalDateTime.now(LOME_ZONE)
        );
    }

    private LocalDateTime safeDateTime(
            Map<String, Object> item,
            boolean futureWhenMissing
    ) {
        LocalDateTime value = dateTime(item);

        if (value != null) {
            return value;
        }

        return futureWhenMissing
                ? LocalDateTime.of(2100, 1, 1, 0, 0)
                : LocalDateTime.of(1900, 1, 1, 0, 0);
    }

    private boolean cityMatches(
            String actual,
            String expected
    ) {
        if (expected == null || expected.isBlank()) {
            return true;
        }

        if (actual == null || actual.isBlank()) {
            return false;
        }

        return normalize(actual)
                .contains(normalize(expected));
    }

    private boolean dateMatches(
            LocalDateTime actual,
            LocalDate expected
    ) {
        if (expected == null) {
            return true;
        }

        return actual != null &&
                actual.toLocalDate().equals(expected);
    }

    private String buildCriteria(
            String departure,
            String destination,
            LocalDate date
    ) {
        StringBuilder criteria = new StringBuilder();

        if (departure != null && !departure.isBlank()) {
            criteria.append(" au départ de ")
                    .append(departure);
        }

        if (destination != null && !destination.isBlank()) {
            criteria.append(" vers ")
                    .append(destination);
        }

        if (date != null) {
            criteria.append(" le ")
                    .append(date.format(DISPLAY_DATE));
        }

        return criteria.toString();
    }

    private boolean hasRole(
            Authentication authentication,
            String role
    ) {
        if (authentication == null) {
            return false;
        }

        String expected = role
                .toUpperCase(Locale.ROOT)
                .replace("ROLE_", "");

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(value -> value
                        .toUpperCase(Locale.ROOT)
                        .replace("ROLE_", ""))
                .anyMatch(expected::equals);
    }

    private List<String> roles(
            Authentication authentication
    ) {
        if (authentication == null) {
            return List.of();
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(value -> value.replace("ROLE_", ""))
                .toList();
    }

    private String paymentStatusOf(
            Map<String, Object> reservation
    ) {
        return string(
                reservation,
                "statutPaiement",
                "paymentStatus",
                "paiementStatut",
                "statusPaiement"
        );
    }

    private boolean hasPaymentInformation(
            Map<String, Object> reservation
    ) {
        return findFirst(
                reservation,
                "statutPaiement",
                "paymentStatus",
                "paiementStatut",
                "statusPaiement",
                "paye",
                "payee",
                "paid",
                "paiementConfirme"
        ).isPresent();
    }

    private boolean isExplicitlyUnpaid(
            Map<String, Object> reservation
    ) {
        String status = paymentStatusOf(reservation)
                .toUpperCase(Locale.ROOT)
                .replace('É', 'E')
                .replace('È', 'E')
                .replace('Ê', 'E');

        if (!status.isBlank()) {
            if (containsPaidStatus(status)) {
                return false;
            }

            return status.contains("NON_PAYE") ||
                    status.contains("NON PAYE") ||
                    status.contains("IMPAYE") ||
                    status.contains("EN_ATTENTE") ||
                    status.contains("EN ATTENTE") ||
                    status.contains("PENDING") ||
                    status.contains("REFUSE") ||
                    status.contains("ECHOUE");
        }

        Optional<Object> paidValue = findFirst(
                reservation,
                "paye",
                "payee",
                "paid",
                "paiementConfirme"
        );

        return paidValue.isPresent() &&
                !booleanValue(
                        reservation,
                        "paye",
                        "payee",
                        "paid",
                        "paiementConfirme"
                );
    }

    private boolean containsPaidStatus(String status) {
        String normalized = status == null
                ? ""
                : status.toUpperCase(Locale.ROOT);

        return normalized.contains("PAYE") ||
                normalized.contains("PAID") ||
                normalized.contains("CONFIRME") ||
                normalized.contains("VALIDE");
    }

    private void addScore(
            Map<AssistantIntent, Integer> scores,
            AssistantIntent intent,
            String text,
            int weight,
            String... expressions
    ) {
        for (String expression : expressions) {
            if (text.contains(expression)) {
                scores.merge(
                        intent,
                        weight,
                        Integer::sum
                );
            }
        }
    }

    private CityPair extractCities(
            String text,
            List<String> availableCities
    ) {
        List<CityPosition> found = new ArrayList<>();

        for (String city : availableCities) {
            if (city == null || city.isBlank()) {
                continue;
            }

            String normalizedCity = normalize(city);
            int index = text.indexOf(normalizedCity);

            if (index >= 0) {
                found.add(new CityPosition(city, index));
            }
        }

        found.sort(Comparator.comparingInt(
                CityPosition::index
        ));

        String departure = null;
        String destination = null;

        if (found.size() >= 2) {
            departure = found.get(0).name();
            destination = found.get(1).name();
        } else if (found.size() == 1) {
            String city = found.get(0).name();
            String normalizedCity = normalize(city);

            if (text.contains("depuis " + normalizedCity) ||
                    text.contains("depart de " + normalizedCity) ||
                    text.contains("de " + normalizedCity + " vers")) {
                departure = city;
            } else {
                destination = city;
            }
        }

        return new CityPair(departure, destination);
    }

    private LocalDate extractDate(String text) {
        LocalDate today = LocalDate.now(LOME_ZONE);

        if (containsAny(text, "apres demain", "apres-demain")) {
            return today.plusDays(2);
        }

        if (containsAny(text, "demain")) {
            return today.plusDays(1);
        }

        if (containsAny(
                text,
                "aujourd hui",
                "ce soir",
                "cet apres midi"
        )) {
            return today;
        }

        Matcher frenchDate = Pattern.compile(
                "\\b(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})\\b"
        ).matcher(text);

        if (frenchDate.find()) {
            String value = frenchDate.group(1) +
                    "/" +
                    frenchDate.group(2) +
                    "/" +
                    frenchDate.group(3);

            try {
                return LocalDate.parse(
                        value,
                        DateTimeFormatter.ofPattern("d/M/uuuu")
                );
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }

        Matcher isoDate = Pattern.compile(
                "\\b(\\d{4})-(\\d{2})-(\\d{2})\\b"
        ).matcher(text);

        if (isoDate.find()) {
            try {
                return LocalDate.parse(isoDate.group());
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }

        return null;
    }

    private Integer extractPassengerCount(String text) {
        Matcher matcher = Pattern.compile(
                "\\b(\\d{1,2})\\s*(place|places|personne|personnes|passager|passagers)\\b"
        ).matcher(text);

        if (!matcher.find()) {
            return null;
        }

        try {
            int value = Integer.parseInt(matcher.group(1));
            return value > 0 ? value : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private boolean isFollowUp(String text) {
        return containsAny(
                text,
                "et apres",
                "et ensuite",
                "ensuite",
                "et combien",
                "combien coute t il",
                "a quelle heure",
                "et demain",
                "et pour kara",
                "et pour sokode",
                "et celui la",
                "et ce trajet",
                "ou dois je aller",
                "comment faire"
        );
    }

    private boolean containsAny(
            String text,
            String... values
    ) {
        for (String value : values) {
            if (text.contains(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsWord(
            String text,
            String word
    ) {
        return Pattern.compile(
                "(^|\\s)" +
                        Pattern.quote(word) +
                        "(\\s|$)"
        ).matcher(text).find();
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(
                value.toLowerCase(Locale.ROOT).trim(),
                Normalizer.Form.NFD
        );

        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized
                .replace('’', '\'')
                .replace('`', '\'')
                .replace('´', '\'');

        normalized = normalized.replaceAll(
                "[^a-z0-9'\\s/-]",
                " "
        );

        normalized = normalized.replaceAll("\\s+", " ");

        return normalized.trim();
    }

    private String formatMoney(Double value) {
        return String.format(
                Locale.FRENCH,
                "%,.0f",
                value
        ).replace('\u202f', ' ');
    }

    private String firstNonBlank(
            String first,
            String second
    ) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private record Analysis(
            AssistantIntent intent,
            boolean greeting,
            String departure,
            String destination,
            LocalDate date,
            Integer passengerCount,
            boolean allTrips,
            boolean bookingHelpRequested
    ) {
    }

    private static class ConversationContext {
        private AssistantIntent lastIntent;
        private String departure;
        private String destination;
        private LocalDate date;
        private String lastEntityId;

        private void merge(Analysis analysis) {
            if (analysis.intent() != AssistantIntent.UNKNOWN &&
                    analysis.intent() != AssistantIntent.GREETING &&
                    analysis.intent() != AssistantIntent.THANKS &&
                    analysis.intent() != AssistantIntent.GOODBYE &&
                    analysis.intent() != AssistantIntent.BOOKING_HELP) {
                lastIntent = analysis.intent();
            }

            if (analysis.departure() != null &&
                    !analysis.departure().isBlank()) {
                departure = analysis.departure();
            }

            if (analysis.destination() != null &&
                    !analysis.destination().isBlank()) {
                destination = analysis.destination();
            }

            if (analysis.date() != null) {
                date = analysis.date();
            }
        }
    }

    private static class CityPair {
        private String departure;
        private String destination;

        private CityPair(
                String departure,
                String destination
        ) {
            this.departure = departure;
            this.destination = destination;
        }
    }

    private record CityPosition(
            String name,
            int index
    ) {
    }
}