package com.service.springbackend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public Map<String, String> publicEndpoint() {
        return Map.of("message", "Dieser Endpunkt ist öffentlich zugänglich.");
    }

    @GetMapping("/private")
    public ResponseEntity<?> testPrivate(@AuthenticationPrincipal Jwt jwt) {
        // Wenn du hier landest, war das Token gültig (ES256 Prüfung bestanden)
        return ResponseEntity.ok("Erfolg! Hallo " + jwt.getClaimAsString("email"));
    }
}