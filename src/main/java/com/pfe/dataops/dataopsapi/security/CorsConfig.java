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

        // 🔹 Fronts autorisés
        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:*",                          // dev
                "https://orange-ground-0b187a01e.3.azurestaticapps.net"  // ⬅️ ton front Azure (à adapter)
        ));

        // 🔹 Méthodes autorisées
        cfg.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // 🔹 Headers autorisés
        cfg.setAllowedHeaders(List.of("*"));

        // 🔹 Expose le header Authorization
        cfg.addExposedHeader("Authorization");

        // 🔹 Autorise l’envoi de cookies / tokens
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
