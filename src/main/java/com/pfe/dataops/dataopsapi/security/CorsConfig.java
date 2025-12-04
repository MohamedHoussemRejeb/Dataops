package com.pfe.dataops.dataopsapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // 🔹 Autorise ton front Angular (peu importe le port)
        cfg.setAllowedOriginPatterns(List.of("http://localhost:*"));

        // 🔹 Méthodes autorisées
        cfg.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // 🔹 Headers autorisés
        cfg.setAllowedHeaders(List.of("*"));

        // 🔹 Expose le header Authorization si Keycloak l'utilise
        cfg.addExposedHeader("Authorization");

        // 🔹 Autorise l’envoi du token à ton API
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
