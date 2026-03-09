package com.service.springbackend.service;

import com.service.springbackend.dto.AuthResponse;
import com.service.springbackend.dto.LoginRequest;
import com.service.springbackend.dto.LoginResponse;
import com.service.springbackend.dto.RegisterRequest;
import com.service.springbackend.model.Role;
import com.service.springbackend.model.User;
import com.service.springbackend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class Authservice {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final EntityManager entityManager;

    Authservice(UserRepository userRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon.key}")
    private String supabaseAnonKey;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String url = supabaseUrl + "/auth/v1/signup";

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseAnonKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "email", request.email().trim(),
                "password", request.password()
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> resBody = response.getBody();

            if (resBody == null) {
                throw new RuntimeException("Supabase Antwort ist leer.");
            }

            String supabaseUuidString = (String) resBody.get("id");
            if (supabaseUuidString == null && resBody.containsKey("user")) {
                Map<String, Object> userMap = (Map<String, Object>) resBody.get("user");
                supabaseUuidString = (String) userMap.get("id");
            }

            if (supabaseUuidString == null) {
                throw new RuntimeException("Konnte keine UUID von Supabase erhalten.");
            }

            UUID supabaseId = UUID.fromString(supabaseUuidString);

            User newUser = new User();
            newUser.setId(supabaseId);
            newUser.setEmail(request.email().trim());
            newUser.setUsername(request.username());
            newUser.setRole(Role.USER);
            User savedUser = userRepository.save(newUser);

            return new AuthResponse(
                    savedUser.getId(),
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    savedUser.getRole().name(),
                    "Registrierung erfolgreich"
            );

        } catch (Exception e) {
            System.err.println("FEHLER BEI REGISTRIERUNG: " + e.getMessage());
            throw e;
        }
    }


    public LoginResponse login(LoginRequest request) {
        String url = supabaseUrl + "/auth/v1/token?grant_type=password";

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseAnonKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "email", request.email(),
                "password", request.password()
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        Map<String, Object> resBody = response.getBody();

        String token = (String) resBody.get("access_token");

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Nutzer in Datenbank nicht gefunden"));

        return new LoginResponse(user.getId() ,user.getEmail(), user.getUsername(), user.getRole(), token);
    }
}