// src/main/java/com/pfe/dataops/dataopsapi/governance/access/KeycloakUserService.java
package com.pfe.dataops.dataopsapi.governance.access;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakUserService {

    @Value("${keycloak.admin.base-url}")
    private String baseUrl;

    @Value("${keycloak.admin.admin-realm}")
    private String adminRealm;

    @Value("${keycloak.admin.target-realm}")
    private String targetRealm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String username;

    @Value("${keycloak.admin.password}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Récupère tous les users du realm target (dataops) avec leurs rôles.
     */
    public List<KeycloakUserDto> findAllUsers() {
        String token = obtainAdminToken();

        // 1️⃣ liste des users du realm cible
        String url = baseUrl + "/admin/realms/" + targetRealm + "/users?max=1000&briefRepresentation=true";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        List<Map<String, Object>> rawUsers = resp.getBody();
        if (rawUsers == null) return List.of();

        List<KeycloakUserDto> result = new ArrayList<>();

        for (Map<String, Object> u : rawUsers) {
            String id = (String) u.get("id");
            String username = (String) u.get("username");
            String firstName = (String) u.getOrDefault("firstName", "");
            String lastName = (String) u.getOrDefault("lastName", "");
            String email = (String) u.getOrDefault("email", "");

            String fullName = (firstName + " " + lastName).trim();
            if (fullName.isEmpty()) {
                fullName = username; // fallback si pas de prénom/nom
            }

            List<String> roles = fetchRealmRoles(token, id);

            result.add(new KeycloakUserDto(id, username, fullName, email, roles));
        }

        return result;
    }

    /**
     * Méthode utilisée par AccessMatrixBuilderService.
     * On délègue simplement à findAllUsers().
     */
    public List<KeycloakUserDto> getAllUsers() {
        return findAllUsers();
    }

    /**
     * Récupère les rôles de realm pour un user.
     */
    private List<String> fetchRealmRoles(String token, String userId) {
        String url = baseUrl + "/admin/realms/" + targetRealm + "/users/" + userId + "/role-mappings/realm";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        List<Map<String, Object>> rolesRaw = resp.getBody();
        if (rolesRaw == null) return List.of();

        List<String> names = new ArrayList<>();
        for (Map<String, Object> r : rolesRaw) {
            Object name = r.get("name");
            if (name != null) {
                names.add(name.toString());
            }
        }
        return names;
    }

    /**
     * Récupère un token admin en password grant sur le realm master.
     */
    private String obtainAdminToken() {
        String url = baseUrl + "/realms/" + adminRealm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);     // ex: admin-cli
        form.add("username", username);      // ex: admin
        form.add("password", password);      // ex: admin21+

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<Map> resp = restTemplate.postForEntity(url, entity, Map.class);
        Map body = resp.getBody();
        if (body == null || body.get("access_token") == null) {
            throw new IllegalStateException("Impossible d’obtenir un token admin Keycloak");
        }
        return body.get("access_token").toString();
    }
}
