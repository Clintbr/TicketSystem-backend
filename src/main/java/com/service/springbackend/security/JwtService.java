package com.service.springbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.function.Function;
/*
@Service
public class JwtService {

    @Value("${supabase.auth.url}")
    private String supabaseAuthUrl;

    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Die JWK-URL von Supabase (Standardpfad)
        String jwksUrl = supabaseAuthUrl + "/.well-known/jwks.json";

        // Wir nutzen einen Locator, der den passenden Schlüssel automatisch aus der URL lädt
        return Jwts.parser()
                .keyLocator(new io.jsonwebtoken.impl.security.EmptyKeyLocator()) // Platzhalter, siehe unten
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

 */