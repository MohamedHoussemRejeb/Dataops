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

        // 🌍 ORIGINES AUTORISÉES — DOIVENT ÊTRE EXACTES
        cfg.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "https://orange-ground-0b187a01e.3.azurestaticapps.net"
        ));

        // 🔁 Méthodes autorisées
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 📨 Headers autorisés
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));

        // 📤 Headers exposés
        cfg.setExposedHeaders(List.of("Authorization"));

        // 🔐 Important pour Keycloak (cookies/tokens)
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);

        return source;
    }
}
