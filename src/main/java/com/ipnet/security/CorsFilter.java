package com.ipnet.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private static final Logger logger =
            LoggerFactory.getLogger(CorsFilter.class);

    /*
     * Origines autorisées à appeler l'API depuis un navigateur.
     */
    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            "http://localhost:4200",
            "https://transia-front-end.essolotiegraceatta.workers.dev"
    );

    public CorsFilter() {
        logger.info("CorsFilter initialisé");
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Aucun traitement nécessaire.
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");

        /*
         * On ne renvoie Access-Control-Allow-Origin
         * que pour une origine connue.
         */
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {

            httpResponse.setHeader(
                    "Access-Control-Allow-Origin",
                    origin
            );

            /*
             * Important lorsque l'origine est dynamique.
             */
            httpResponse.setHeader(
                    "Vary",
                    "Origin"
            );
        }

        httpResponse.setHeader(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        );

        httpResponse.setHeader(
                "Access-Control-Allow-Headers",
                "Authorization, Content-Type, Accept, Origin, "
                        + "X-Requested-With, X-XSRF-TOKEN"
        );

        httpResponse.setHeader(
                "Access-Control-Max-Age",
                "3600"
        );

        /*
         * Transia utilise JWT dans le header Authorization.
         * Nous n'utilisons pas de cookie de session
         * cross-origin.
         */
        httpResponse.setHeader(
                "Access-Control-Allow-Credentials",
                "false"
        );

        /*
         * Une requête OPTIONS correspond au preflight CORS.
         * On la valide immédiatement.
         */
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {

            httpResponse.setStatus(
                    HttpServletResponse.SC_OK
            );

            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Aucun traitement nécessaire.
    }
}