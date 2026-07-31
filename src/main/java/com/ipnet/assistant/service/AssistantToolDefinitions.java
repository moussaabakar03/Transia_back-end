package com.ipnet.assistant.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AssistantToolDefinitions {

    private final List<Map<String, Object>> tools;

    public AssistantToolDefinitions() {
        this.tools = buildTools();
    }

    public List<Map<String, Object>> all() {
        return tools;
    }

    private List<Map<String, Object>> buildTools() {
        List<Map<String, Object>> result = new ArrayList<>();

        result.add(function(
                "search_trips",
                "Recherche les trajets réels disponibles. À utiliser pour les horaires, prix, places, départs, destinations et dates.",
                objectSchema(
                        properties(
                                "departure", nullableString("Ville de départ ou null si non précisée."),
                                "destination", nullableString("Ville d'arrivée ou null si non précisée."),
                                "date", nullableString("Date ISO AAAA-MM-JJ ou null."),
                                "passenger_count", nullableInteger("Nombre de places demandé ou null.")
                        ),
                        List.of("departure", "destination", "date", "passenger_count")
                )
        ));

        result.add(function(
                "get_next_trip",
                "Récupère le prochain trajet du client ou du chauffeur connecté.",
                objectSchema(
                        properties(
                                "perspective", enumString(
                                        "Point de vue à utiliser.",
                                        List.of("auto", "client", "chauffeur")
                                )
                        ),
                        List.of("perspective")
                )
        ));

        result.add(function(
                "get_my_reservations",
                "Récupère uniquement les réservations du client connecté.",
                objectSchema(
                        properties(
                                "scope", enumString(
                                        "Filtre des réservations.",
                                        List.of("all", "upcoming", "unpaid", "paid", "cancelled")
                                )
                        ),
                        List.of("scope")
                )
        ));

        result.add(function(
                "get_payment_status",
                "Vérifie le paiement d'une réservation appartenant au client connecté.",
                objectSchema(
                        properties(
                                "reservation_id", nullableString("UUID de réservation ou null pour la prochaine réservation pertinente.")
                        ),
                        List.of("reservation_id")
                )
        ));

        result.add(function(
                "get_ticket",
                "Récupère le billet QR et les sièges d'une réservation du client connecté.",
                objectSchema(
                        properties(
                                "reservation_id", nullableString("UUID de réservation ou null pour la prochaine réservation pertinente.")
                        ),
                        List.of("reservation_id")
                )
        ));

        result.add(function(
                "check_refund_eligibility",
                "Vérifie la règle de remboursement au moins 48 heures avant le départ.",
                objectSchema(
                        properties(
                                "reservation_id", nullableString("UUID de réservation ou null pour la prochaine réservation pertinente.")
                        ),
                        List.of("reservation_id")
                )
        ));

        result.add(function(
                "get_tracking",
                "Récupère le statut de suivi et la dernière position GPS autorisée d'un trajet.",
                objectSchema(
                        properties(
                                "reservation_id", nullableString("UUID de réservation du client ou null."),
                                "trip_id", nullableString("UUID du trajet du chauffeur ou null.")
                        ),
                        List.of("reservation_id", "trip_id")
                )
        ));

        result.add(function(
                "get_my_parcels",
                "Récupère les colis expédiés par le client ou les livraisons affectées au livreur connecté.",
                objectSchema(
                        properties(
                                "perspective", enumString(
                                        "Point de vue à utiliser.",
                                        List.of("auto", "client", "livreur")
                                ),
                                "status", nullableString("Statut de colis à filtrer ou null.")
                        ),
                        List.of("perspective", "status")
                )
        ));

        result.add(function(
                "get_next_parcel",
                "Récupère le prochain colis actif du client ou du livreur connecté.",
                objectSchema(
                        properties(
                                "perspective", enumString(
                                        "Point de vue à utiliser.",
                                        List.of("auto", "client", "livreur")
                                )
                        ),
                        List.of("perspective")
                )
        ));

        result.add(function(
                "get_chauffeur_passengers",
                "Récupère la liste d'embarquement d'un trajet uniquement pour son chauffeur affecté.",
                objectSchema(
                        properties(
                                "trip_id", nullableString("UUID du trajet ou null pour le prochain trajet du chauffeur.")
                        ),
                        List.of("trip_id")
                )
        ));

        result.add(function(
                "get_profile",
                "Récupère le profil autorisé de l'utilisateur connecté.",
                objectSchema(Map.of(), List.of())
        ));

        result.add(function(
                "list_agencies",
                "Liste les agences Transia réelles, éventuellement filtrées par ville.",
                objectSchema(
                        properties(
                                "city", nullableString("Nom de ville ou null pour toutes les agences.")
                        ),
                        List.of("city")
                )
        ));

        result.add(function(
                "get_app_instructions",
                "Retourne les étapes exactes d'utilisation de l'application pour une procédure générale.",
                objectSchema(
                        properties(
                                "topic", enumString(
                                        "Sujet de la procédure.",
                                        List.of("booking", "payment", "ticket", "refund", "tracking", "parcel", "profile", "general")
                                )
                        ),
                        List.of("topic")
                )
        ));

        return List.copyOf(result);
    }

    private Map<String, Object> function(
            String name,
            String description,
            Map<String, Object> parameters
    ) {
        Map<String, Object> tool = new LinkedHashMap<>();
        tool.put("type", "function");
        tool.put("name", name);
        tool.put("description", description);
        tool.put("parameters", parameters);
        tool.put("strict", true);
        return tool;
    }

    private Map<String, Object> objectSchema(
            Map<String, Object> properties,
            List<String> required
    ) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", required);
        schema.put("additionalProperties", false);
        return schema;
    }

    private Map<String, Object> properties(Object... entries) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < entries.length; index += 2) {
            result.put(entries[index].toString(), entries[index + 1]);
        }
        return result;
    }

    private Map<String, Object> nullableString(String description) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", List.of("string", "null"));
        schema.put("description", description);
        return schema;
    }

    private Map<String, Object> nullableInteger(String description) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", List.of("integer", "null"));
        schema.put("description", description);
        schema.put("minimum", 1);
        return schema;
    }

    private Map<String, Object> enumString(
            String description,
            List<String> values
    ) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "string");
        schema.put("description", description);
        schema.put("enum", values);
        return schema;
    }
}
