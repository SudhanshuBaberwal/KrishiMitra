package com.krishimitra.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "KrishiMitra AI API",
                version = "1.0.0",
                description = """
                        KrishiMitra AI is a multilingual agricultural assistance platform.

                        The API provides farmer profile management, crop management,
                        soil records, weather forecasts, market prices, official
                        agricultural advisories, AI assistance and dashboard data.

                        Authentication uses JWT Bearer tokens.
                        """,
                contact = @Contact(
                        name = "KrishiMitra API Support"
                ),
                license = @License(
                        name = "Proprietary"
                )
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter your JWT access token. Do not include the Bearer prefix."
)
public class OpenApiConfig {
}