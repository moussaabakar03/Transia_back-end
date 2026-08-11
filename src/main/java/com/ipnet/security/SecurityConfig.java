package com.ipnet.security;

import com.ipnet.security.jwt.AuthEntryPointJwt;
import com.ipnet.security.jwt.AuthTokenFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserService userService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authenticationFilter;

    public SecurityConfig(
            UserService userService,
            AuthEntryPointJwt unauthorizedHandler,
            AuthTokenFilter authenticationFilter
    ) {
        this.userService = userService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                /*
                 * L’application utilise une authentification JWT.
                 * Les sessions HTTP et les jetons CSRF ne sont donc
                 * pas utilisés.
                 */
                .csrf(AbstractHttpConfigurer::disable)

                /*
                 * Réponse renvoyée lorsqu’un utilisateur non authentifié
                 * tente d’accéder à une ressource sécurisée.
                 */
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                unauthorizedHandler
                        )
                )

                /*
                 * Aucune session utilisateur n’est conservée côté serveur.
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Endpoints publics.
                         */
                        .requestMatchers(
                                "/",
                                "/api/v1/login",
                                "/api/v1/register",
                                "/api/v1/forgot-password",
                                "/api/v1/reset-password",
                                "/error",
                                "/csrf",
                                "/resources/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/import/**",
                                "/etats/**",
                                "/datasource/**"
                        )
                        .permitAll()

                        /*
                         * Assistant TransIA.
                         *
                         * Le chatbot doit recevoir un token JWT afin de
                         * consulter les trajets, réservations, paiements,
                         * profils et colis de l’utilisateur connecté.
                         *
                         * Tous les rôles authentifiés peuvent l’utiliser :
                         * CLIENT, CHAUFFEUR, LIVREUR, AGENT_ACCUEIL,
                         * ADMIN_AGENCE et SUPER_ADMIN.
                         */
                        .requestMatchers(
                                "/api/v1/assistant/**"
                        )
                        .authenticated()

                        /*
                         * Administration des rôles.
                         * Seul le super administrateur peut intervenir.
                         */
                        .requestMatchers(
                                "/api/roles/**"
                        )
                        .hasRole("SUPER_ADMIN")

                        /*
                         * Administration des utilisateurs et historique
                         * administratif.
                         */
                        .requestMatchers(
                                "/api/admin/**",
                                "/api/v1/users/**",
                                "/api/v1/role",
                                "/api/v1/history"
                        )
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "ADMIN_AGENCE"
                        )

                        /*
                         * Création d’un signalement par un chauffeur.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/chauffeur-problemes",
                                "/api/v1/chauffeur-problemes/"
                        )
                        .hasRole("CHAUFFEUR")

                        /*
                         * Consultation des signalements chauffeur.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/chauffeur-problemes/**"
                        )
                        .hasAnyRole(
                                "CHAUFFEUR",
                                "AGENT_ACCUEIL",
                                "ADMIN_AGENCE",
                                "SUPER_ADMIN"
                        )

                        /*
                         * Endpoints propres au chauffeur.
                         */
                        .requestMatchers(
                                "/api/v1/chauffeur/**",
                                "/api/v1/chauffeurs/me/**"
                        )
                        .hasRole("CHAUFFEUR")

                        /*
                         * Gestion administrative des livreurs.
                         */
                        .requestMatchers(
                                "/api/v1/livreurs/**"
                        )
                        .hasAnyRole(
                                "AGENT_ACCUEIL",
                                "ADMIN_AGENCE",
                                "SUPER_ADMIN"
                        )

                        /*
                         * Endpoints utilisés directement par le livreur.
                         */
                        .requestMatchers(
                                "/api/v1/livreur/**"
                        )
                        .hasRole("LIVREUR")

                        /*
                         * Les tournées peuvent être consultées ou gérées
                         * selon le rôle de l’utilisateur.
                         */
                        .requestMatchers(
                                "/api/v1/tournees/**"
                        )
                        .hasAnyRole(
                                "LIVREUR",
                                "AGENT_ACCUEIL",
                                "ADMIN_AGENCE",
                                "SUPER_ADMIN"
                        )

                        /*
                         * Envoi d’un feedback par un client.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/feedback/submit"
                        )
                        .hasRole("CLIENT")

                        /*
                         * Consultation administrative des feedbacks.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/feedback/all"
                        )
                        .hasAnyRole(
                                "AGENT_ACCUEIL",
                                "ADMIN_AGENCE",
                                "SUPER_ADMIN"
                        )

                        /*
                         * Toutes les autres API exigent au minimum
                         * une authentification JWT.
                         *
                         * Les règles plus précises peuvent aussi être
                         * définies dans les contrôleurs avec @PreAuthorize.
                         */
                        .anyRequest()
                        .authenticated()
                )

                /*
                 * En-têtes HTTP de sécurité.
                 */
                .headers(headers -> headers
                        .frameOptions(
                                HeadersConfigurer
                                        .FrameOptionsConfig::sameOrigin
                        )
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true)
                        )
                        .xssProtection(xss -> xss.headerValue(
                                XXssProtectionHeaderWriter
                                        .HeaderValue
                                        .ENABLED_MODE_BLOCK
                        ))
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives(
                                        "default-src 'self'; "
                                                + "script-src 'self'; "
                                                + "style-src 'self'; "
                                                + "font-src 'self'; "
                                                + "img-src 'self' data:; "
                                                + "connect-src 'self'"
                                )
                        )
                )

                /*
                 * Fournisseur utilisé pour authentifier les utilisateurs
                 * à partir de UserService et des mots de passe BCrypt.
                 */
                .authenticationProvider(
                        authenticationProvider()
                )

                /*
                 * Le filtre JWT doit être exécuté avant le filtre standard
                 * de Spring Security.
                 */
                .addFilterBefore(
                        authenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration
                .getAuthenticationManager();
    }

}