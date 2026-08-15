package com.ipnet.security;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.ErrorResponse;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "TransIA",
                        email = "transia@gmail.com"
                ),
                title = "Back-end TransIA",
                description = "API REST de la plateforme TransIA",
                version = "1.0",
                license = @License(
                        name = "Licence TransIA"
                ),
                termsOfService = "Terms of service"
        ),

        servers = {
                @Server(
                        description = "Production - Render",
                        url = "https://transia-back-end.onrender.com"
                ),
                @Server(
                        description = "Development - Local",
                        url = "http://localhost:8181"
                )
        },

        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)

@SecurityScheme(
        name = "bearerAuth",
        description = "Authentification JWT TransIA",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)

public class OpenAPIConfig {

    @Bean
    public OpenApiCustomizer schemaCustomizer() {

        ResolvedSchema resolvedSchema = ModelConverters
                .getInstance()
                .resolveAsResolvedSchema(
                        new AnnotatedType(ErrorResponse.class)
                );

        return openApi -> {
            if (resolvedSchema.schema != null
                    && resolvedSchema.schema.getName() != null) {

                openApi.schema(
                        resolvedSchema.schema.getName(),
                        resolvedSchema.schema
                );
            }
        };
    }
}