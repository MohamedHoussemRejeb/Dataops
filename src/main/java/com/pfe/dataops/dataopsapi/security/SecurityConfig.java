// src/main/java/com/pfe/dataops/dataopsapi/security/SecurityConfig.java
package com.pfe.dataops.dataopsapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
                // ✅ use the CorsConfigurationSource bean from CorsConfig
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth

                        // ✅ allow CORS preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ make /api/health public as well
                        .requestMatchers("/actuator/health", "/api/health", "/api/public/**").permitAll()

                        // 🔓 WebSockets / SockJS
                        .requestMatchers("/api/ws-events/**", "/api/ws-notifications/**").permitAll()
                        .requestMatchers("/api/notifications/test").permitAll()

                        // 🔓 dev endpoints
                        .requestMatchers("/api/settings/**", "/api/catalog/**", "/api/etl/**", "/api/chatbot/**").permitAll()
                        .requestMatchers("/api/lineage/**").permitAll()
                        .requestMatchers("/api/lineage/column-edges/**").permitAll()
                        .requestMatchers("/api/lineage/columns/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/governance/access-matrix/rebuild").permitAll()

                        // le reste des /api/** reste protégé
                        .requestMatchers("/api/**").authenticated()

                        // autres (assets, etc.)
                        .anyRequest().permitAll()
                )
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

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            Object roles = realmAccess.get("roles");
            if (roles instanceof List<?>) {
                for (Object role : (List<?>) roles) {
                    if (role instanceof String roleName) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()));
                    }
                }
            }
        }

        return authorities;
    }
}
