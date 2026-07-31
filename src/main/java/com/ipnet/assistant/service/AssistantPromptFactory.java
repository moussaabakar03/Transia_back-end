package com.ipnet.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipnet.security.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class AssistantPromptFactory {

    private static final ZoneId LOME_ZONE = ZoneId.of("Africa/Lome");

    private final ObjectMapper objectMapper;

    public AssistantPromptFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String build(
            User user,
            Authentication authentication,
            Map<String, Object> uiContext
    ) {
        String roles = authentication == null
                ? "AUCUN"
                : authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(value -> value.replace("ROLE_", ""))
                .sorted()
                .reduce((left, right) -> left + ", " + right)
                .orElse("AUCUN");

        String currentDateTime = ZonedDateTime.now(LOME_ZONE)
                .format(DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm",
                        Locale.FRENCH
                ));

        String contextJson = safeJson(uiContext);

        return """
                Tu es TransIA, l'assistant officiel de l'application Transia de transport interurbain au Togo.

                CONTEXTE UTILISATEUR
                - Date et heure au Togo : %s
                - Utilisateur connecté : %s
                - Rôles : %s
                - Contexte facultatif de l'interface : %s

                RÈGLES OBLIGATOIRES
                1. Réponds toujours en français clair, naturel et concis.
                2. Comprends les formulations libres, les fautes simples, les pronoms et les questions de suivi.
                3. Pour toute donnée actuelle ou personnelle (trajets, prix, places, réservations, paiement, billet, remboursement, suivi GPS, colis, profil, agences), utilise l'outil approprié. N'invente jamais une donnée.
                4. Les résultats des outils sont la seule source de vérité pour les données Transia.
                5. Ne révèle jamais les données d'un autre utilisateur. Respecte le rôle connecté.
                6. Tu peux répondre sans outil aux salutations et aux explications générales, mais utilise get_app_instructions pour les procédures propres à l'application.
                7. Ne prétends jamais avoir créé, annulé, payé ou remboursé quelque chose : les outils disponibles sont en lecture seule.
                8. Si une information manque, explique précisément ce qui manque et propose une action utile.
                9. Ne parle ni d'OpenAI, ni de modèle, ni de fonction interne à l'utilisateur.
                10. Le champ actionId doit être choisi seulement parmi : OPEN_TRIPS, OPEN_RESERVATIONS, OPEN_RESERVATION, OPEN_TICKET, OPEN_REFUND, OPEN_TRACKING, OPEN_PARCELS, OPEN_PARCEL, OPEN_PROFILE, OPEN_AGENCIES, OPEN_CHAUFFEUR_TRIP, ou null.
                11. Les suggestions doivent être courtes, pertinentes et au nombre maximal de quatre.
                12. Pour « demain », « aujourd'hui » et les dates relatives, utilise la date du Togo donnée ci-dessus.
                13. Ne recopie pas inutilement des données personnelles dans la réponse. Ne montre un téléphone, un e-mail, un QR code ou une position GPS que si la demande le nécessite.

                FORMAT FINAL
                Retourne une réponse structurée conforme au schéma fourni par l'API. Le champ message doit suffire à répondre même si l'application n'exécute pas actionId.
                """.formatted(
                currentDateTime,
                user == null ? "Inconnu" : safe(user.getNom()),
                roles,
                contextJson
        );
    }

    private String safeJson(Map<String, Object> value) {
        if (value == null || value.isEmpty()) {
            return "{}";
        }

        try {
            String json = objectMapper.writeValueAsString(value);
            return json.length() > 1200 ? json.substring(0, 1200) : json;
        } catch (JsonProcessingException exception) {
            return "{}";
        }
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Inconnu" : value;
    }
}
