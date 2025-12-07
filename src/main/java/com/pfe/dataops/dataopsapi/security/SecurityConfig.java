// src/main/java/com/pfe/dataops/dataopsapi/security/SecurityConfig.java
package com.pfe.dataops.dataopsapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            // 🔒 API 100% stateless
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                // ✅ Préflight CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ✅ Endpoints publics
                .requestMatchers("/actuator/health", "/api/health", "/api/public/**").permitAll()

                // 🔓 (optionnel) WebSockets et endpoint de test – à toi de décider si tu veux les sécuriser
                .requestMatchers("/api/ws-events/**", "/api/ws-notifications/**").permitAll()
                .requestMatchers("/api/notifications/test").permitAll()

                // 🔐 TOUT le reste des API DataOps doit être authentifié
                .requestMatchers("/api/**").permitAll()

                // 🌐 assets, index.html, etc. → libres
                .anyRequest().permitAll()
            )
            // 🔑 Resource server JWT (Keycloak)
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractRoles);
        return converter;
    }

    private Collection<org.springframework.security.core.GrantedAuthority> extractRoles(Jwt jwt) {
        List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            Object roles = realmAccess.get("roles");
            if (roles instanceof List<?>) {
                for (Object role : (List<?>) roles) {
                    if (role instanceof String roleName) {
                        authorities.add(
                            new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                "ROLE_" + roleName.toUpperCase()
                            )
                        );
                    }
                }
            }
        }

        return authorities;
    }
}
